/*
 * Anything: A flexible and easy to use data structure that combines the 
 * characteristics of a list and a hashmap into one data structure.
 * 
 * Based on a paper by Peter Sommerlad and Marcel Rueedi on 
 * 'Do-it-yourself reflection' entered at EuroPLOP 1998.
 * 
 * This library was written completely from scratch and is based only 
 * in spirit on the original Java Anything implementation written by 
 * Andre Weinand/Marcel Rueedi in 1997/99 and enhanced by Stefan Tramm 
 * in 2006.
 * 
 * Copyright (c) 2008 Thomas Marti and others. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package risk.commons.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;


/**
 * Externe Klasse, die als "doppelseitige HashMap" funktioniert.
 * @author Andre Weinand/Marcel Rueedi, enhanced by Stefan Tramm
 */
public class BidiMap implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Map<String, Integer> keyToIndex = new HashMap<String, Integer>();
	private final SortedMap<Integer, String> indexToKey = new TreeMap<Integer, String>();

	public int size() {
		return keyToIndex.size();
	}

	public boolean isEmpty() {
		return (size() == 0);
	}
	
	public boolean containsKey(String key) {
		return keyToIndex.containsKey(key);
	}

	public boolean containsValue(int value) {
		return indexToKey.containsKey(value);
	}

	public Integer get(String key) {
		return keyToIndex.get(key);
	}

	public String get(int index) {
		return indexToKey.get(index);
	}

	public void put(int index, String key) {
		keyToIndex.put(key, index);
		indexToKey.put(index, key);
	}

	public Integer remove(String key) {
		Integer index = keyToIndex.remove(key);
		if (index != null) {
			indexToKey.remove(index);
		}
		return index;
	}

	public String remove(int index) {
		String key = indexToKey.remove(index);
		if (key != null) {
			keyToIndex.remove(key);
		}
		return key;
	}
	
	public void shiftUp(int fromIndex, int shiftCount) {
		if (isEmpty()) return;
		
		SortedMap<Integer, String> tailMap = new TreeMap<Integer, String>(indexToKey.tailMap(fromIndex));
		while(!tailMap.isEmpty()) {
			Integer index = tailMap.lastKey();
			String key = tailMap.get(index);
			
			keyToIndex.put(key, (index + shiftCount));
			indexToKey.remove(index);
			indexToKey.put((index + shiftCount), key);
			
			tailMap.remove(index);
		}
	}

	public void shiftDown(int fromIndex, int shiftCount) {
		if (isEmpty()) return;

		SortedMap<Integer, String> tailMap = new TreeMap<Integer, String>(indexToKey.tailMap(fromIndex));
		for (Entry<Integer, String> entry : tailMap.entrySet()) {
			int index = entry.getKey();
			String key = entry.getValue();
			
			keyToIndex.put(key, (index - shiftCount));
			indexToKey.remove(index);
			indexToKey.put((index - shiftCount), key);
		}
	}

	public List<String> keys() {
		return new ArrayList<String>(indexToKey.values());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BidiMap)) return false;

		BidiMap other = (BidiMap)obj;
		assert keyToIndex.equals(other.keyToIndex) == indexToKey.equals(other.indexToKey);

		return keyToIndex.equals(other.keyToIndex);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("{");
		
		for (Iterator<Entry<Integer, String>> it = indexToKey.entrySet().iterator(); it.hasNext();) {
			Entry<Integer, String> entry = it.next();
			builder.append("'").append(entry.getValue()).append("' <=> ").append(entry.getKey());
			if (it.hasNext()) {
				builder.append(", ");				
			}
		}
		
		builder.append("}");
		
		return builder.toString();
	}
	
}