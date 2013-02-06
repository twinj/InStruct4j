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
package com.github.twinj.instruct.delegates;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class Flags<E extends Enum<?> & Flags.EnumFlags> {
	
	BitSet flags;

	private Flags(BitSet flags) {
		this.flags = flags;
	}
	
	public interface EnumFlags {
		public Integer getMask();
	}
	
	public void set(E flag) {
		flags.set(flag.ordinal());
	}
	
	public Boolean get(E flag) {
		return flags.get(flag.ordinal());
	}
	
	public void combine(Integer flag) {
		
	}
	
	@SuppressWarnings("rawtypes")
	public static Flags<?> valueOf(Integer flag) {
		byte[] barray = new byte[4];
		ByteBuffer.wrap(barray).order(ByteOrder.LITTLE_ENDIAN).putInt(flag);		
		return new Flags(BitSet.valueOf(barray));
	}
	

}
