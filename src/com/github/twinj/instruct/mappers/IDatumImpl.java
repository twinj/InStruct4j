package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatum;


public abstract class IDatumImpl <R> implements IDatum<R> {

	protected Integer sizeOf = 0;
	//protected R value = null;
	protected Integer id = 0;

	@Override
	public Integer sizeOf() {
		return sizeOf;
	}
	
	@Override
	public R value() {
//		if (value == null) {
//			value = resolve();
//		}
		return resolve();
	}
	
	@Override
	public Class<?> getType() {
		return this.getClass();
	}
}
