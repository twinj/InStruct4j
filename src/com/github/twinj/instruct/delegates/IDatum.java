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

import java.nio.ByteOrder;

public interface IDatum<R> {
	
	static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
	static final int SIZE_0F_BYTE_IN_BITS = 8;
	static final int SIZE_OF_PARAGRAPH = 16;
	static final int SIZE_OF_PAGE = 512;
	
	static final int BYTE = 1; // unsigned byte
	static final int SCHAR = 1; // utf-8 string char
	static final int WORD = 2; // unsigned short
	static final int DWORD = 4; // unsigned int
	static final int QUAD = 8; // unsigned long
	
	public byte[] bytes();
	public Integer sizeOf();
	public R value();
	public R resolve();
	public Class<?> getType();
	
	public String toHexString();
}