package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;



public class TByte extends ATDatum {

	public static final Integer SIZE_OF = 1;

	public TByte() {
		super(SIZE_OF);
	}

	@Override
	public Number resolve() {
		return IDatumInfo.toSInteger(bytes);
	}
}
