package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;


public abstract class TString extends DatumAbstract<String> {
	
	public TString(Integer sizeOf) {
		super(sizeOf);
	}

	public IDatumInfo create(Enum<?> req, String name) {
		return new IDatumInfo(this, req, name);
	}
	
	public IDatumInfo create(Integer arraySize, String name) {
		return new IDatumInfo(this, arraySize, name);
	}
}
