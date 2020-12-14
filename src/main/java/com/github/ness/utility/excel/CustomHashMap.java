package com.github.ness.utility.excel;

import java.util.ArrayList;
import java.util.List;

public class CustomHashMap {
	private final List<HashMapValue> list;

	public List<HashMapValue> getList() {
		return list;
	}

	public CustomHashMap() {
		list = new ArrayList<HashMapValue>();
	}

	public void putIfAbsent(String s1, ArrayList<String> b) {
		HashMapValue v = new HashMapValue(s1, b);
		if (!this.list.contains(v)) {
			this.list.add(v);
		}
	}

	public HashMapValue getHashMapValue(int index) {
		return this.list.get(index);
	}

	public void put(String s1, ArrayList<String> b) {
		HashMapValue v = new HashMapValue(s1, b);
		this.list.add(v);
	}

	public void remove(String s1) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).key.equals(s1)) {
				list.remove(i);
			}
		}
	}

	public ArrayList<String> get(String s1) {
		for (HashMapValue v : list) {
			if (v.key.equals(s1)) {
				return v.value;
			}
		}
		return null;
	}

	public class HashMapValue {
		private final String key;
		private final ArrayList<String> value;

		public String getKey() {
			return key;
		}

		public ArrayList<String> getValue() {
			return value;
		}

		public HashMapValue(String key, ArrayList<String> value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof HashMapValue))
				return false;
			HashMapValue other = (HashMapValue) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		private CustomHashMap getOuterType() {
			return CustomHashMap.this;
		}

		@Override
		public String toString() {
			return "HashMapValue [key=" + key + ", value=" + value + "]";
		}

	}

}
