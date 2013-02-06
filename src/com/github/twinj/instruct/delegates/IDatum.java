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