package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;



public class TSShort extends ATDatum {
	
	public static final Integer SIZE_OF = 2;

	public TSShort() {
		super(SIZE_OF);
	}
	
	@Override
	public Short resolve() {
		return  IDatumInfo.toSShort(bytes);
	}
	
	@Override
	public String toHexString() {
		return Integer.toHexString(value().shortValue()).toUpperCase();
	}
}
