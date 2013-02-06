package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;



public class TDWord extends ATDatum {

	public static final Integer SIZE_OF = 4;

	public TDWord() {
		super(SIZE_OF);
	}
	
	@Override
	public Number resolve() {
		return  IDatumInfo.toUInteger(bytes);
	}
	
	@Override
	public String toHexString() {
		return Integer.toHexString(value().intValue()).toUpperCase();
	}
}
