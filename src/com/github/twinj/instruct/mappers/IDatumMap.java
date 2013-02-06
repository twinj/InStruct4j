package com.github.twinj.instruct.mappers;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.github.twinj.instruct.delegates.IDatum;
import com.github.twinj.instruct.delegates.Requisite;
import com.github.twinj.instruct.parser.TParser.THandle;

import static com.github.twinj.instruct.mappers.IDatumMap.State.*;

/**
 * 
 * @author Daniel Kemp
 */
public abstract class IDatumMap<K, V, R, P extends THandle> extends IDatumImpl<R>
			implements
				Map<K, V> {
	
	// Static Datums
	protected static final TNull NULL = new TNull();
	protected static final TByte BYTE = new TByte();
	
	protected static final TWord WORD = new TWord();
	protected static final TSShort SHORT = new TSShort();
	
	protected static final TDWord DWORD = new TDWord();
	protected static final TSInt INT = new TSInt();
	
	protected static final TQuad QUAD = new TQuad();
	protected static final TSLong LONG = new TSLong();
	
	protected static final TUTFChar UTFCHAR = new TUTFChar();
	
	protected static final ATFlags FLAGSDWORD = new TFlagsDWord();
	protected static final ATFlags FLAGSQUAD = new TFlagsQuad();
	
	// Static structs
	protected final static boolean VERBOSE = false;
	
	protected static final int INNER_STRUCT = -1;
	protected static final int UNRESOLVED = -1;
	
	protected Class<K> keyType;
	protected Boolean staticSize = false;
	protected Map<K, V> map;
	
	public IDatumMap(Class<K> keyType, Map<K, V> m) {
		this.map = m;
		this.keyType = keyType;
	}
	
	public IDatumMap(Class<K> keyType, Map<K, V> m, Integer sizeOf) {
		this(keyType, m);
		this.sizeOf = sizeOf;
		this.staticSize = true;
	}
	
	public IDatumMap(Class<K> keyType) {
		this.keyType = keyType;
	}
	
	public IDatumMap(Class<K> keyType, Integer sizeOf) {
		this(keyType);
		this.sizeOf = sizeOf;
		this.staticSize = true;
	}
	
	protected enum State {
		INITIAL,
		MAP_RESOLVED_GROUP,
		UNRESOLVED_SIZE,
		COMPLETE
	}
	
	class PHelp {
		State state = INITIAL;
		
		int groupSize = 0;
		int current = 0;		
		int nextInnerStruct = UNRESOLVED;
		
		int size = sizeOf;
		int arraySize = 1;
		//int offset = 0;

		K[] properties = getValuesAsArray();
				
		protected PHelp(P handle, Requisite<?> req) {
			if (size == 0) {
				resolveNextGroupSize(handle, this, req);
			} else {
				groupSize = Integer.MAX_VALUE;
			}
		}
		protected void reset() {
			this.size = 0;
			this.groupSize = this.current;
			this.state = INITIAL;
		}
		protected int size() {
			return this.properties.length;
		}
		
		protected void resetArraySize() {
			this.arraySize = 1;
		}
		
		protected void increaseBufferSize(int size) {
			this.size += size;
		}
		
		protected void goBack() {
			this.groupSize--;
		}
		
		protected Boolean datumSizeResolved() {
			return this.nextInnerStruct != UNRESOLVED;
		}
		
		protected void resetNextInnerStructPredicate() {
			this.nextInnerStruct = UNRESOLVED;
		}
		
		protected void mapInnerStruct() {
			this.state = MAP_RESOLVED_GROUP;
		}
		
		protected void mapInnerStructNext() {
			this.state = MAP_RESOLVED_GROUP;
			this.nextInnerStruct = this.groupSize;
			this.groupSize--;
		}
		
		protected void setSizeUnresolved() {
			this.state = UNRESOLVED_SIZE;
		}
	}
	
	protected abstract K[] getValuesAsArray();
	
	@SuppressWarnings({
				"rawtypes", "unchecked"
	})
	public static IDatumMap parse(Class<?> clazz, THandle h) throws IOException {
		try {
			return ((IDatumMap) clazz.newInstance()).create(h, 0, null);
		} catch (InstantiationException | IllegalAccessException ignore) {}
		return null;
	}
	
	
	@SuppressWarnings({
				"rawtypes", "unchecked"
	})
	public static IDatumMap parse(Class<?> clazz, THandle handle, Requisite<?> req)
				throws IOException {
		try {
			return ((IDatumMap) clazz.newInstance()).create(handle, 0, req);
		} catch (InstantiationException | IllegalAccessException ignore) {}
		return null;
	}
	
	
	@SuppressWarnings({
				"rawtypes", "unchecked"
	})
	public static IDatumMap parse(THandle handle, Class<?> clazz, Integer id)
				throws IOException {
		try {
			return ((IDatumMap) clazz.newInstance()).create(handle, id, null);
		} catch (InstantiationException | IllegalAccessException ignore) {}
		return null;
	}
	
	
	@SuppressWarnings("rawtypes")
	protected IDatumMap create(P handle, Integer id, final Requisite<?> req)
				throws IOException {
		
		SeekableByteChannel fc = handle.getChannel();
		
		Integer eOff = entryOffset(handle);
		if (eOff != INNER_STRUCT) {
			fc.position(eOff);
		}
		
		PHelp t = new PHelp(handle, req);
		this.setId(id);
		
		while (t.state != COMPLETE) {
			
			switch (t.state) {
				case UNRESOLVED_SIZE: break;
				case INITIAL :
				case MAP_RESOLVED_GROUP : {
						byte[] bytes = new byte[t.size * t.arraySize];
						handle.setBuffer(ByteBuffer.wrap(bytes));
						handle.getBuffer().order(ByteOrder.LITTLE_ENDIAN);
						fc.read(handle.getBuffer());
						handle.getBuffer().rewind();
						break;
				}
				default :
			}			
			mapGroupProperties(handle, t, req);
			resolveNextGroupSize(handle, t, req);
		}
		return this;
	}
	
	@SuppressWarnings("rawtypes")
	protected IDatumMap create(P handle) throws IOException {
		PHelp t = new PHelp(handle, null);
		mapGroupProperties(handle, t, null);
		return this;
	}
	
	public Object valueOf(Enum<?> p) {
		IDatum<?> h = (IDatum<?>) get(p);
		return h != null ? h.value() : null;
	}
	
	@Override
	public Integer sizeOf() {
		return sizeOf == null ? 0 : sizeOf;
	}
	
	public Class<K> getEnumType() {
		return this.keyType;
	}
	
	public void setSize(Integer sizeOf) {
		if (this.staticSize == false) {
			this.sizeOf = sizeOf;
		}
	}
	
	protected abstract void init();
	
	public abstract Integer entryOffset(P h);
	
	protected abstract void resolveNextGroupSize(P h, PHelp t, Requisite<?> reqSuper);
	
	protected abstract void mapGroupProperties(P h, PHelp t, Requisite<?> reqSuper);
	
	@Override
	public int size() {
		return map.size();
	}
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	
	@Override
	public V get(Object key) {
		return map.get(key);
	}
	
	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}
	
	@Override
	public V remove(Object key) {
		return map.remove(key);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}
	
	@Override
	public void clear() {
		map.clear();
	}
	
	@Override
	public Set<K> keySet() {
		return map.keySet();
	}
	
	@Override
	public Collection<V> values() {
		return map.values();
	}
	
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}
	
	public Integer id() {
		return getId();
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
}
