package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;



public class TUnicodeChar extends TString {

		public static final Integer SIZE_OF = 2; // character size

		public TUnicodeChar() {
			super(SIZE_OF);
		}
		
		@Override
		public String resolve() {
			return IDatumInfo.toUnicodeString(bytes);
		}
	}
