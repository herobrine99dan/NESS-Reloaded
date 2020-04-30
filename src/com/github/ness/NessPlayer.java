package com.github.ness;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NessPlayer {

	private int suspicion;
	
	private Set<Long> clickHistory = ConcurrentHashMap.newKeySet();
	
	public void click() {
		clickHistory.add(System.currentTimeMillis());
	}
	
}
