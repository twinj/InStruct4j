package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;



public class TSInt extends ATDatum {

	public static final Integer SIZE_OF = 4;

	public TSInt() {
		super(SIZE_OF);
	}
	
	@Override
	public Number resolve() {
		return  IDatumInfo.toSInteger(bytes);
	}
	
	@Override
	public String toHexString() {
		return Integer.toHexString(value().intValue()).toUpperCase();
	}
}
