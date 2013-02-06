package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;
import com.github.twinj.instruct.mappers.DatumAbstract;

public abstract class ATScript extends DatumAbstract<String>  {
	
	public static final Integer SIZE_OF = 1;
	
	public ATScript() {
		super(SIZE_OF);
	}
	
	@Override
	public String resolve() {
		return this.toHexString();
	}

	public IDatumInfo create(Enum<?> req, String name) {
		return new IDatumInfo(this, req, name);
	}
	
	public IDatumInfo create(Integer arraySize, String name) {
		return new IDatumInfo(this, arraySize, name);
	}

}
