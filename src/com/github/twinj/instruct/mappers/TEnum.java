/*******************************************************************************
 * Copyright 2013 Daniel Kemp
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
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
