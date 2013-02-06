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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.LinkedHashMap;

import com.github.twinj.instruct.delegates.IDatum;
import com.github.twinj.instruct.delegates.IDatumInfo;
import com.github.twinj.instruct.delegates.Requisite;
import com.github.twinj.instruct.parser.TParser.THandle;

/**
 * @author Daniel Kemp
 * 
 */
public abstract class ATStructTable<K, V extends IDatumImpl<?>, P extends THandle>
			extends
				IDatumMap<K, V, String, P> {
	
	Class<V> valueType;
	
	public ATStructTable(Class<K> keyType, Class<V> valueType) {
		super(keyType);
		this.valueType = valueType;
	}
	
	public ATStructTable(Class<K> keyType, Class<V> valueType, Integer sizeOf) {
		super(keyType, sizeOf);
		this.valueType = valueType;
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
		for (IDatum<?> d : values()) {
			ret.put(d.bytes());
		}
		return ret.array();
	}
	
	@SuppressWarnings({
				"rawtypes", "unchecked"
	})
	public static ATStructTable parse(Class<?> clazz, THandle h, Integer arraySize)
				throws IOException {
		try {
			return ((ATStructTable) clazz.newInstance()).create(arraySize, h);
		} catch (InstantiationException | IllegalAccessException ignore) {}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	protected ATStructTable create(Integer arraySize, P handle) throws IOException {
		
		SeekableByteChannel fc = handle.getChannel();
		Integer eOff = entryOffset(handle);
		PHelp t = new PHelp(handle, null);
		this.map = new LinkedHashMap<K, V>(arraySize);
		fc.position(eOff);
		this.sizeOf = 0;
		t.size = sizeOf;
		t.arraySize = 0;
		
		while (arraySize > t.arraySize) {
			mapGroupProperties(handle, t, null);
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void mapGroupProperties(P h, PHelp t, Requisite<?> reqSuper) {
		V s = null;
		try {
			s = (V) ATStruct.parse(h, valueType, t.arraySize);			
			sizeOf += s.sizeOf();
			s.id = this.id++;
			put(key(s), s);
			t.arraySize++;
		} catch (IOException ex) {}
	}
	
	@Override
	protected void resolveNextGroupSize(P h, PHelp t, Requisite<?> reqSuper) {
		return;
	}
	
	protected abstract K key(V v);
	
	@Override
	protected K[] getValuesAsArray() {
		return null;
	}
}
