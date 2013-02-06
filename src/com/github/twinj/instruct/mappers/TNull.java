package com.github.twinj.instruct.mappers;


public class TNull extends ATDatum {

	public static final Integer SIZE_OF = 0;

	public TNull() {
		super(SIZE_OF);
	}

	@Override
	public Number resolve() {
		return 0;
	}
}
