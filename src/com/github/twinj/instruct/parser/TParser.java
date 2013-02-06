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
package com.github.twinj.instruct.parser;

import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public abstract class TParser<P extends TParser.THandle> {
	
	protected static final int BYTE_SIZE_IN_BITS = 8;
	protected static final long CLEAR_BITS_LONG = 0xFFFFFFFFL;
	protected static final int CLEAR_BITS_INT = 0xFFFF;
	protected static final int CLEAR_BITS_BYTE = 0xFF;
	
	protected final P instance;
	
	public interface Strategy<P extends TParser.THandle> {
		void parse(final P parser);
	}
	
	public static class THandle {
		public ByteBuffer buffer = null;
		public SeekableByteChannel ch;
		
		protected THandle() {}
		
		public SeekableByteChannel getChannel() {
			return ch;
		}
		
		public void setChannel(SeekableByteChannel ch) {
			this.ch = ch;
		}
		
		public ByteBuffer getBuffer() {
			return buffer;
		}
		
		public void setBuffer(ByteBuffer wrap) {
			buffer = wrap;
		}
		
		public Long readDWord() {
			return buffer.getInt() & CLEAR_BITS_LONG;
		}
		
		public Long readDWord(int position) {
			buffer.position(position);
			return buffer.getInt() & CLEAR_BITS_LONG;
		}
		
		public Integer readWord() {
			return buffer.getShort() & CLEAR_BITS_INT;
		}
		
		public Integer readWord(int position) {
			buffer.position(position);
			return buffer.getShort() & CLEAR_BITS_INT;
		}
		
		@SuppressWarnings("unused")
		private Number readBuffer(int position, int sizeOf) {
			byte[] barray = new byte[sizeOf];
			buffer.position(position);
			buffer.get(barray);
			if (sizeOf <= 4) {
				return valueOfInteger(barray);
			} else {
				return valueOfLong(barray);
			}
		}
	
	}
	
	public TParser(P handle) {
		instance = handle;
	}
	
	public P getInstance() {
		return instance;
	}
	
	public P getParser() {
		return instance;
	}
	
	public void parse(Strategy<P> strategy) {
		strategy.parse(instance);
	}
	
	public static Integer valueOfInteger(byte[] bytes) {
		int v = (int) bytes[0] & CLEAR_BITS_BYTE;
		// shifing to fit bit array into an int
		for (int i = 1; i < 4; i++) {
			v = v | ((int) bytes[i] & 0xFF) << (i * BYTE_SIZE_IN_BITS);
		}
		return new Integer(v);
	}
	
	public static Long valueOfLong(byte[] bytes) {
		long v = (long) bytes[0] & CLEAR_BITS_BYTE;
		// shifing to fit bit array into an int
		for (int i = 1; i < 8; i++) {
			v = v | ((long) bytes[i] & CLEAR_BITS_BYTE) << (i * BYTE_SIZE_IN_BITS);
		}
		return new Long(v);
	}
}
