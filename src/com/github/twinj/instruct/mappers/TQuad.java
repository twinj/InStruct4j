package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;

public class TQuad extends ATDatum {

	public static final Integer SIZE_OF = 8;
	
	public TQuad() {
		super(SIZE_OF);
	}
	
	@Override
	public Number resolve() {		
		return IDatumInfo.toULong(bytes);
	}	
}
