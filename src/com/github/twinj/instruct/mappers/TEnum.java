package com.github.twinj.instruct.mappers;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import com.github.twinj.instruct.delegates.IDatumInfo;


public class TEnum<E extends Enum<E>> extends DatumAbstract<E> {
	
	public static final Integer SIZE_OF = 1;
	public Class<?> type;
	
	public TEnum() {
		super(SIZE_OF);
	}
	
	@Override
	public E resolve() {
		@SuppressWarnings("unchecked")
		E[] values = (E[]) valuesAsArray(type);
		return values[IDatumInfo.toSInteger(bytes)];
	}
	
	public IDatumInfo create(String name, Class<E> type) {
		IDatumInfo d = new IDatumInfo(this, name);
		d.eNumtype = type;
		return d;
	}
	
	protected static <K extends Enum<K>> Collection<K> values(Class<?> keyType) {
		try {
			Method valuesMethod = keyType.getMethod("values", new Class[0]);
			@SuppressWarnings("unchecked")
			K[] values = (K[]) valuesMethod.invoke(null, new Object[0]);
			return Arrays.asList(values);
		} catch (Exception ex) {
			throw new RuntimeException("Exceptions here should be impossible", ex);
		}
	}
	
	protected static <K extends Enum<K>> K[] valuesAsArray(Class<?> keyType) {
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
