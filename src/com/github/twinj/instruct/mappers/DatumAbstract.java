package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;


public abstract class DatumAbstract<R> extends IDatumImpl<R> {
	
	protected byte[] bytes;
	//protected Long offset;
	
	public DatumAbstract(Integer sizeOf) {
		this.sizeOf = sizeOf;
	}
	
	@Override
	public byte[] bytes() {
		return bytes;
	}

	public String toHexString() {
		return IDatumInfo.toHexString(bytes);
	}
}