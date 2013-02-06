package com.github.twinj.instruct.mappers;

import java.util.Arrays;
import java.util.BitSet;

import com.github.twinj.instruct.delegates.IDatumInfo;


public abstract class ATFlags extends DatumAbstract<BitSet> {
	
	public ATFlags(Integer sizeOf) {
		super(sizeOf);
	}
	
	@Override
	public BitSet resolve() {
		// little endian
		byte[] smaller = null;
		if (bytes.length == 8) {
			smaller = Arrays.copyOfRange(bytes, 4, 8);
		} else {
			smaller = bytes;
		}
		return BitSet.valueOf(smaller);
	}
	
	public IDatumInfo create(String name) {
		return new IDatumInfo(this, name);
	}
	
}
