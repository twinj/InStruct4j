package com.github.twinj.instruct.delegates;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;

public class Flags<E extends Enum<?> & Flags.EnumFlags> {
	
	BitSet flags;

	private Flags(BitSet flags) {
		this.flags = flags;
	}
	
	public interface EnumFlags {
		public Integer getMask();
	}
	
	public void set(E flag) {
		flags.set(flag.ordinal());
	}
	
	public Boolean get(E flag) {
		return flags.get(flag.ordinal());
	}
	
	public void combine(Integer flag) {
		
	}
	
	@SuppressWarnings("rawtypes")
	public static Flags<?> valueOf(Integer flag) {
		byte[] barray = new byte[4];
		ByteBuffer.wrap(barray).order(ByteOrder.LITTLE_ENDIAN).putInt(flag);		
		return new Flags(BitSet.valueOf(barray));
	}
	

}
