package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;




public class TUTFChar extends TString {

	public static final Integer SIZE_OF = 1; // character size

	public TUTFChar() {
		super(SIZE_OF);
	}
	
	@Override
	public String resolve() {
		return IDatumInfo.toUtfString(bytes);
	}
}
