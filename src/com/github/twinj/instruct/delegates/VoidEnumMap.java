package com.github.twinj.instruct.delegates;

import com.github.twinj.instruct.mappers.ATStruct;


public enum VoidEnumMap implements ATStruct.Accessor {
	;

	@Override
	public IDatumInfo getDatum() {
		return null;
	}
	
}
