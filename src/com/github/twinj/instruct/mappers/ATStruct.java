package com.github.twinj.instruct.mappers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;

import com.github.twinj.instruct.delegates.IDatum;
import com.github.twinj.instruct.delegates.IDatumInfo;
import com.github.twinj.instruct.delegates.Requisite;
import com.github.twinj.instruct.parser.TParser.THandle;

import static com.github.twinj.instruct.mappers.IDatumMap.State.*;

/**
 * 
 * @author Daniel Kemp
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class ATStruct<K extends Enum<K> & ATStruct.Accessor, P extends THandle>
			extends
				IDatumMap<K, IDatum, String, P> {
	
	public interface Accessor {
		IDatumInfo getDatum();
	}
	
	public interface Condition {
		public Status assertCondition(THandle h, ATStruct<?, ?> d, Requisite<?> req);
	}
	
	public interface PreCondition extends Condition {
		
	}
	
	public interface PostCondition extends Condition {}
	
	public enum Status {
		CONTINUE,
		CONTINUE_AND_THEN_STOP,
		SKIP,
		STOP;
	}
	
	public ATStruct(Class<K> keyType) {
		super(keyType, new EnumMap<K, IDatum>(keyType));
	}
	
	public ATStruct(Class<K> keyType, Integer sizeOf) {
		super(keyType, new EnumMap<K, IDatum>(keyType), sizeOf);
	}
	
	public String toUtfString(K p) {
		return IDatumInfo.toUtfString(get(p).bytes());
	}
	
	@Override
	public String toHexString() {
		return null;
	}
	
	public Integer valueOfBM(K p, int bitMask) {
		DatumAbstract<?> h = (DatumAbstract<?>) get(p);
		return ((Integer) (h.value()) & bitMask);
	}
	
	@Override
	public byte[] bytes() {
		ByteBuffer ret = ByteBuffer.wrap(new byte[sizeOf()]);
		for (K p : values(keyType)) {
			ret.put(get(p).bytes());
		}
		return ret.array();
	}
	
	public IDatumInfo create(Integer arraySize, String name) {
		return new IDatumInfo(this, arraySize, name);
	}
	
	public IDatumInfo create(String name) {
		return new IDatumInfo(this, name);
	}
	
	public IDatumInfo create(Enum<?> requisite, String name) {
		return new IDatumInfo(this, requisite, name);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void mapGroupProperties(P h, PHelp t, Requisite<?> reqSuper) {
		
		Boolean continueThenStop = false;
		
		for (; t.current <= t.groupSize && t.current < t.properties.length; t.current++) {
			Integer req = 0;
			IDatumInfo d = t.properties[t.current].getDatum();
			Condition c = d.condition;
			Boolean brake = false;
			Requisite<Object> reqChild = null;
			
			if (d.requisite != null) {
				reqChild = new Requisite<Object>(this.valueOf((K) d.requisite));
			}
			
			switch (c.assertCondition(h, this, reqSuper)) {
			
				case CONTINUE :
					mapDatum(h, t, req, d, reqChild);
					if (d.postCondition != null) {
						d.postCondition.assertCondition(h, this, reqChild);
					}
					if (continueThenStop) brake = true;
					break;
				case CONTINUE_AND_THEN_STOP :
					continueThenStop = true;
				case SKIP :
					continue;
				case STOP :
					brake = true;
			}
			if (brake) break;
		}
	}
	
	@SuppressWarnings({
		"unchecked"
	})
	public void mapDatum(THandle h, PHelp t, Integer size, IDatumInfo d,
				Requisite<?> reqSuper) {
		
		switch (d.type()) {
			case IS_DATUM :
			case IS_SCRIPT :				
			case IS_STRING : {
				size = d.sizeOf;
				// If a datum has a requisite it is for a string array
				if (d.hasRequisiteProperty()) {
					size *= ((Number) get(d.requisite).value()).intValue();
				}
				if (size != 0) {
					createDatum(h, t, size, d);
				}
				break;
			}
			case IS_STRUCT : {
				IDatum s = null;
				try {
					if (d.sizeOf == 0) {
						s = ((IDatumMap) d.clazz.newInstance()).create(h, 0, reqSuper);
						t.resetNextInnerStructPredicate();
					} else {
						s = ((IDatumMap) d.clazz.newInstance()).create(h);
					}
				} catch (InstantiationException | IllegalAccessException | IOException ex) {}
				size += s.sizeOf();
				put(t.properties[t.current], s);
				break;
			}
			case IS_ARRAY : {
				TStructArray s = null;
				if (d.hasRequisiteProperty()) {
					t.arraySize = ((Number) valueOf(d.requisite)).intValue();
				} else {
					t.arraySize = d.strategy.parse(h);
				}
				s = new TStructArray(d.clazz, t.arraySize);
				
				for (int i = 0; i < t.arraySize; i++) {
					try {
						((TStructArray) s).add(ATStruct.parse(d.clazz, h, reqSuper));
					} catch (IOException ex) {}
					size += s.sizeOf();
				}
				put(t.properties[t.current], s);
				t.resetArraySize();
				break;
			}
			case IS_FLAGS : {
				createDatum(h, t, d.sizeOf, d);
				break;
			}
			case IS_ENUM : {
				((TEnum<?>) createDatum(h, t, d.sizeOf, d)).type = d.eNumtype;
				break;
			}
			default :
				break;
		}
		
		setSize(size() + size);
		
		if (VERBOSE) {
			if (size != 0) System.err.print(t.properties[t.current] + ": "
						+ get(t.properties[t.current]).value() + " size[" + size + "]\n");
		}
	}
	
	@Override
	protected void resolveNextGroupSize(P h, PHelp t, Requisite<?> reqSuper) {
		
		Boolean continueThenStop = false;
		Boolean currentGroupResolved = false;
		t.reset();
		
		for (; t.groupSize < t.properties.length; t.groupSize++) {
			
			IDatumInfo d = ((Accessor) t.properties[t.groupSize]).getDatum();
			
			switch (d.condition.assertCondition(h, this, reqSuper)) {
				case CONTINUE :
					currentGroupResolved = resolveGroupSize(t, d, h);
					if (continueThenStop) {
						currentGroupResolved = true;
					}
					break;
				case CONTINUE_AND_THEN_STOP :
					continueThenStop = true;
				case SKIP :
					continue;
				case STOP :
					currentGroupResolved = true;
			}
			if (currentGroupResolved) {
				break;
			}
		}
		
		if (t.size == 0 && t.state == INITIAL) {
			t.state = COMPLETE;
		} else {
			// t.offset += t.size;
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean resolveGroupSize(PHelp t, IDatumInfo d, P h) {
		switch (d.type()) {
			case IS_DATUM :
			case IS_SCRIPT :
			case IS_STRING : {
				if (d.hasRequisiteProperty()) {
					// If null requisite value it has not been resolved: end here
					if (valueOf((K) d.requisite) != null) {
						Integer reqSize = ((Number) valueOf((K) d.requisite)).intValue();
						t.increaseBufferSize(reqSize * d.sizeOf);
						if (reqSize == 0) {
							t.setSizeUnresolved();
						}
					} else {
						t.goBack();
					}
					return true;
				}
				t.increaseBufferSize(d.sizeOf);
				return false;
			}
			case IS_STRUCT : {
				if (d.sizeOf == 0) {
					if (t.datumSizeResolved()) {
						t.mapInnerStruct();
					} else { // end here remove struct calculate priors
						t.mapInnerStructNext();
					}
					return true;
				} else {
					t.increaseBufferSize(d.sizeOf);
					return false;
				}
			}
			case IS_ARRAY : {
				if (t.datumSizeResolved()) {
					t.mapInnerStruct();
				} else {
					t.mapInnerStructNext();
				}
				return true;
			}
			case IS_FLAGS :
			case IS_ENUM : {
				t.increaseBufferSize(d.sizeOf);
			}
			default :
				return false;
		}
	}
	@Override
	protected K[] getValuesAsArray() {
		return valuesAsArray(keyType);
	}
	
	private DatumAbstract<?> createDatum(THandle h, PHelp t, Integer size, IDatumInfo d) {
		byte[] barray = new byte[size];
		h.buffer.get(barray);
		DatumAbstract<?> da = null;
		try {
			da = (DatumAbstract<?>) d.clazz.newInstance();
			da.bytes = barray;
			// da.offset = (long) h.buffer.position();
			put(t.properties[t.current], (DatumAbstract<?>) da);
		} catch (InstantiationException | IllegalAccessException ignore) {}
		return da;
	}
	
	public static <K extends Enum<K>> Collection<K> values(Class<K> keyType) {
		try {
			Method valuesMethod = keyType.getMethod("values", new Class[0]);
			@SuppressWarnings("unchecked")
			K[] values = (K[]) valuesMethod.invoke(null, new Object[0]);
			return Arrays.asList(values);
		} catch (Exception ex) {
			throw new RuntimeException("Exceptions here should be impossible", ex);
		}
	}
	
	public static <K extends Enum<K>> K[] valuesAsArray(Class<K> keyType) {
		try {
			Method valuesMethod = keyType.getMethod("values", new Class[0]);
			@SuppressWarnings("unchecked")
			K[] values = (K[]) valuesMethod.invoke(null, new Object[0]);
			return values;
		} catch (Exception ex) {
			throw new RuntimeException("Exceptions here should be impossible", ex);
		}
	}
}
