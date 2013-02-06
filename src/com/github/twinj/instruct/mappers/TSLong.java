package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;



public class TSLong extends ATDatum {

	public static final Integer SIZE_OF = 8;
	
	public TSLong() {
		super(SIZE_OF);
	}
	
	@Override
	public Long resolve() {		
		return IDatumInfo.toSLong(bytes);
	}	
}
