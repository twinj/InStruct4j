package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;


public abstract class ATDatum extends DatumAbstract<Number> {
	
	public ATDatum(Integer sizeOf) {
		super(sizeOf);
	}
	
	public IDatumInfo create(String name) {
		return new IDatumInfo(this, name);
	}
	
	public IDatumInfo create(Integer arraySize, String name) {
		return new IDatumInfo(this, name, arraySize);
	}
	
	public IDatumInfo create(Enum<?> requisite, String name) {
		return new IDatumInfo(this, name, requisite);
	}
}
