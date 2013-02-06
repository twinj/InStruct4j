package com.github.twinj.instruct.mappers;

import com.github.twinj.instruct.delegates.IDatumInfo;
import com.github.twinj.instruct.parser.TParser.THandle;

/**
 * 
 * @author Daniel Kemp
 */
public abstract class ATGuid<P extends THandle> extends ATStruct<ATGuid.Property, P> {
	
	public enum Property implements ATStruct.Accessor {
		
		DATA_1(DWORD.create("First Part")),
		DATA_2(WORD.create("Second Part")),
		DATA_3(WORD.create("Third Part")),
		DATA_4(BYTE.create(8, "Fourth Part"));
		
		IDatumInfo datum;
		
		Property(IDatumInfo datum) {
			this.datum = datum;
		}
		
		@Override
		public IDatumInfo getDatum() {
			return datum;
		}
	}
	
	public static final Integer SIZE_OF = 16;
	
	public ATGuid() {
		super(Property.class, SIZE_OF);
	}
	
	@Override
	public String resolve() {
		String s1 = get(Property.DATA_1).toHexString();
		String s2 = get(Property.DATA_2).toHexString();
		String s3 = get(Property.DATA_3).toHexString();
		
		String ret = String.format("%s%s-%s-%s-%s-%s%s", "{", 
					s1.length() < 8 ? addZeroes(s1, 8) : s1, 
					s2.length() < 4 ? addZeroes(s2, 4) : s2, 
					s3.length() < 4 ? addZeroes(s3, 4) : s3, 
					get(Property.DATA_4).toHexString().substring(0, 4),
					get(Property.DATA_4).toHexString().substring(4), "}");
		return ret;
	}

	
	private static String addZeroes(String guidPart, Integer size) {
		String ret = "";
		
		for (int i = 0; i < size - guidPart.length(); i ++) {
			ret += "0";
		}
		ret += guidPart;
		return ret;
	}	
}
