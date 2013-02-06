/*******************************************************************************
 * Copyright 2013 Daniel Kemp
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.github.twinj.instruct.mappers;

import java.util.ArrayList;

import com.github.twinj.instruct.delegates.IDatum;
import com.github.twinj.instruct.delegates.IDatumInfo;



/**
 * @author Daniel Kemp
 * 
 */
@SuppressWarnings({
			"rawtypes", "serial"
})
public class TStructArray extends ArrayList<IDatumMap> implements IDatum<String> {
	
	Class<?> recordType;
	
	public TStructArray() {
		super(0);
	}
	
	public TStructArray(Class<?> valueType) {
		super(0);
		this.recordType = valueType;
	}
	
	public TStructArray(Class<?> valueType, Integer arraySize) {
		super(arraySize);
		this.recordType = valueType;
	}
	
	public IDatumInfo create(Class<?> clazz, Enum<?> requisite, String name) {
		this.recordType = clazz;
		return new IDatumInfo(this, requisite, name);
	}
	
	public IDatumInfo create(Class<?> clazz, String name, IDatumInfo.Strategy st) {
		this.recordType = clazz;
		return new IDatumInfo(this, name, st);
	}
	
	public IDatumInfo create(Class<?> clazz, String name, Integer arraySize) {
		this.recordType = clazz;
		return new IDatumInfo(this, arraySize, name);
	}
	
	@Override
	public byte[] bytes() {
		return new byte[0];
	}

	@Override
	public Integer sizeOf() {
		return 0;
	}
	
	@Override
	public String toHexString() {
		return "Struct Array";
	}

	@Override
	public String value() {
		return "Struct Array";
	}

	@Override
	public String resolve() {
		String ret = new String();
		
		for (IDatumMap d : this) {
					ret += "\n\t\t" + ": " + d.resolve();	
		}
		return ret;
	}
	
	@Override
	public Class<?> getType() {
		return this.recordType;
	}
	
 public ArrayList<?> getList() {
	 return this;
 }
 
 public interface ArrayTable<R> {
	 public ArrayList<R> get();
 }
}
