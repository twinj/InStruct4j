package com.github.twinj.instruct.delegates;

import static com.github.twinj.instruct.delegates.IDatumInfo.Flag.IS_ARRAY;
import static com.github.twinj.instruct.delegates.IDatumInfo.Flag.IS_DATUM;
import static com.github.twinj.instruct.delegates.IDatumInfo.Flag.IS_ENUM;
import static com.github.twinj.instruct.delegates.IDatumInfo.Flag.IS_FLAGS;
import static com.github.twinj.instruct.delegates.IDatumInfo.Flag.IS_SCRIPT;
import static com.github.twinj.instruct.delegates.IDatumInfo.Flag.IS_STRING;
import static com.github.twinj.instruct.delegates.IDatumInfo.Flag.IS_STRUCT;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import com.github.twinj.instruct.delegates.Flags.EnumFlags;
import com.github.twinj.instruct.mappers.ATDatum;
import com.github.twinj.instruct.mappers.ATFlags;
import com.github.twinj.instruct.mappers.ATScript;
import com.github.twinj.instruct.mappers.ATStruct;
import com.github.twinj.instruct.mappers.ATStruct.Status;
import com.github.twinj.instruct.mappers.TEnum;
import com.github.twinj.instruct.mappers.TString;
import com.github.twinj.instruct.mappers.TStructArray;
import com.github.twinj.instruct.parser.TParser.THandle;

public class IDatumInfo {
	public Integer sizeOf;
	public Integer offset;
	@SuppressWarnings("unchecked")
	public Flags<Flag> flags = (Flags<Flag>) Flags.valueOf(0);
	
	public String name;
	public Class<?> clazz;
	public Class<?> eNumtype;
	public Enum<?> requisite;
	
	public ATStruct.Condition condition = new ATStruct.Condition() {
		@SuppressWarnings("rawtypes")
		public Status assertCondition(THandle handle, ATStruct d, Requisite req) {
			return ATStruct.Status.CONTINUE;
		}
	};
	
	public ATStruct.Condition postCondition = null;
	
	@SuppressWarnings("rawtypes")
	public Strategy strategy = null;
	public boolean display = true;
	public boolean skip = false;
	
	public interface Strategy<P extends THandle> {
		public Integer parse(P handle);
	}
	
	public enum Flag implements EnumFlags {
		IS_DATUM,
		IS_STRUCT,
		IS_STRING,
		IS_ARRAY,
		IS_ENUM,
		IS_FLAGS,
		IS_SCRIPT,
		DONT_DISPLAY,
		DONT_CACHE,
		SKIP,
		GARBAGE_CLEAN;
		
		public int MASK;
		
		Flag() {
			this.MASK = 1 << this.ordinal();
		}
		
		@Override
		public Integer getMask() {
			return MASK;
		}
	}
	
	/**********************************************************************/
	
	private IDatumInfo(IDatum<?> type, Integer sizeOf, String name, Enum<?> requistie) {
		this.name = name;
		this.clazz = (Class<?>) type.getType();
		this.requisite = requistie;
		this.sizeOf = sizeOf;
	}
	
	/**********************************************************************/
	
	private IDatumInfo(ATDatum type, String name, Integer arraySize, Enum<?> requisite) {
		this(type, type.sizeOf() * arraySize, name, requisite);
		this.flags.set(IS_DATUM);
	}
	
	public IDatumInfo(ATDatum type, String name, Integer arraySize) {
		this(type, name, arraySize, null);
	}
	
	public IDatumInfo(ATDatum type, String name, Enum<?> requisite) {
		this(type, name, 1, requisite);
	}
	
	public IDatumInfo(ATDatum type, String name) {
		this(type, name, 1, null);
	}
	
	/**********************************************************************/
	
	private IDatumInfo(TString type, Integer arraySize, Enum<?> req, String name) {
		this(type, arraySize * type.sizeOf(), name, req);
		this.flags.set(IS_STRING);
	}
	
	public IDatumInfo(TString type, Enum<?> req, String name) {
		this(type, 1, req, name);
	}
	
	public IDatumInfo(TString type, Integer arraySize, String name) {
		this(type, arraySize, null, name);
	}
	
	/**********************************************************************/
	
	@SuppressWarnings("rawtypes")
	private IDatumInfo(TStructArray type, String name, Enum<?> req, Integer arraySize,
				Strategy st) {
		this(type, 0, name, req);
		this.strategy = st;
		this.flags.set(IS_ARRAY);
	}
	
	public IDatumInfo(TStructArray type, Enum<?> req, String name) {
		this(type, name, req, 0, null);
	}
	
	@SuppressWarnings("rawtypes")
	public IDatumInfo(TStructArray type, String name, Strategy st) {
		this(type, name, null, 0, st);
	}
	
	public IDatumInfo(TStructArray type, Integer arraySize, String name) {
		this(type, name, null, 0, null);
	}
	
	/**********************************************************************/
	
	private IDatumInfo(ATStruct<?, ?> type, Integer arraySize, Enum<?> req, String name) {
		this(type, arraySize * type.sizeOf(), name, req);
		this.flags.set(IS_STRUCT);
	}
	
	public IDatumInfo(ATStruct<?, ?> type, Integer arraySize, String name) {
		this(type, arraySize, null, name);
	}
	
	public IDatumInfo(ATStruct<?, ?> type, Enum<?> req, String name) {
		this(type, 1, req, name);
	}
	
	public IDatumInfo(ATStruct<?, ?> type, String name) {
		this(type, 1, null, name);
	}
	
	/**********************************************************************/
	
	public IDatumInfo(TEnum<?> type, String name) {
		this(type, type.sizeOf(), name, null);
		this.flags.set(IS_ENUM);
	}
	
	/**********************************************************************/
	
	public IDatumInfo(ATFlags type, String name) {
		this(type, type.sizeOf(), name, null);
		this.flags.set(IS_FLAGS);
	}
	
	/**********************************************************************/
	
	private IDatumInfo(ATScript type, Integer arraySize, Enum<?> requisite, String name) {
		this(type, type.sizeOf() * arraySize, name, requisite);
		this.flags.set(IS_SCRIPT);
	}
	
	public IDatumInfo(ATScript type, Integer arraySize, String name) {
		this(type, arraySize, null, name);
	}
	
	public IDatumInfo(ATScript type, Enum<?> requisite, String name) {
		this(type, 1, requisite, name);
	}
	
	/**********************************************************************/
	
	@SuppressWarnings("unchecked")
	public IDatumInfo flags(Integer flag) {
		Flag type = type();
		this.flags = (Flags<Flag>) Flags.valueOf(flag | type.MASK);
		return this;
	}
	
	public Boolean isStruct() {
		return this.flags.get(IS_STRUCT);
	}
	public Boolean isArray() {
		return this.flags.get(IS_ARRAY);
	}
	public Boolean isDatum() {
		return this.flags.get(IS_DATUM);
	}
	public Boolean isString() {
		return this.flags.get(IS_STRING);
	}
	public Boolean isScript() {
		return this.flags.get(IS_SCRIPT);
	}
	public Boolean isEnum() {
		return this.flags.get(IS_ENUM);
	}
	public Boolean isFlags() {
		return this.flags.get(IS_FLAGS);
	}
	
	public Boolean hasRequisiteProperty() {
		return this.requisite != null;
	}
	
	public Boolean hasStrategy() {
		return this.strategy != null;
	}
	
	public Flag type() {
		if (isDatum()) return IS_DATUM;
		if (isStruct()) return IS_STRUCT;
		if (isString()) return IS_STRING;
		if (isArray()) return IS_ARRAY;
		if (isFlags()) return IS_FLAGS;
		if (isEnum()) return IS_ENUM;
		if (isScript()) return IS_SCRIPT;
		
		return null;
	}
	
	public static int toSInteger(byte[] bytes) {
		int v = ((int) bytes[0]) & 0xFF;
		for (int i = 1; i < bytes.length; i++) {
			v = v | (((int) bytes[i]) & 0xFF) << (i * ATDatum.SIZE_0F_BYTE_IN_BITS);
		}
		return v;
	}
	
	public static long toSLong(byte[] bytes) {
		long v = ((long) bytes[0]) & 0xFF;
		for (int i = 1; i < bytes.length; i++) {
			v = v | (((long) bytes[i]) & 0xFF) << (i * ATDatum.SIZE_0F_BYTE_IN_BITS);
		}
		return v;
	}
	
	public static BigInteger toULong(byte[] bytes) {
		for (int i = 0; i < bytes.length / 2; i++) {
			byte temp = bytes[i];
			bytes[i] = bytes[bytes.length - i - 1];
			bytes[bytes.length - i - 1] = temp;
		}
		return new BigInteger(1, bytes);
	}
	
	public static short toSShort(byte[] bytes) {
		int v = ((int) bytes[0]) & 0xFF;
		for (int i = 1; i < bytes.length; i++) {
			v = v | (((int) bytes[i]) & 0xFF) << (i * ATDatum.SIZE_0F_BYTE_IN_BITS);
		}
		return (short) v;
	}
	
	public static long toUInteger(byte[] bytes) {
		long v = ((long) bytes[0]) & 0xFF;
		for (int i = 1; i < bytes.length; i++) {
			v = v | (((long) bytes[i]) & 0xFF) << (i * ATDatum.SIZE_0F_BYTE_IN_BITS);
		}
		return v;
	}
	
	public static int toUShort(byte[] bytes) {
		int v = ((int) bytes[0]) & 0xFF;
		for (int i = 1; i < bytes.length; i++) {
			v = v | (((int) bytes[i]) & 0xFF) << (i * ATDatum.SIZE_0F_BYTE_IN_BITS);
		}
		return v;
	}
	
	public static String toUtfString(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8").trim();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String toUnicodeString(byte[] bytes) {
		try {
			return new String(bytes, "UTF-8").trim();
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String toHexString(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes);
	}
}
