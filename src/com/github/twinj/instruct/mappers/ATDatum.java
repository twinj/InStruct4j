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

import com.github.twinj.instruct.delegates.IDatumInfo;


public abstract class ATDatum extends DatumAbstract<Number> {
	
	public ATDatum(Integer sizeOf) {
		super(sizeOf);
	}
	
	public IDatumInfo create(String name) {
		return new IDatumInfo(this, name);
	}
	
	public IDatumInfo create(Integer arraySize, String name) {
		return new IDatumInfo(this, name, arraySize);
	}
	
	public IDatumInfo create(Enum<?> requisite, String name) {
		return new IDatumInfo(this, name, requisite);
	}
}
