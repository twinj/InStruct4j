package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;



public class TWord extends ATDatum {
	
	public static final Integer SIZE_OF = 2;
	
	public TWord() {
		super(SIZE_OF);
	}
	
	@Override
	public Number resolve() {
		return IDatumInfo.toUShort(bytes);
	}
	
	public String toHexString() {
		return Integer.toHexString(value().shortValue() & 0xffff).toUpperCase();
	}
}
