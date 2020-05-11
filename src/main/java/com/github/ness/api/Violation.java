package com.github.ness.api;

import lombok.Getter;

public class Violation {

	@Getter
	private final String check;
	@Getter
	private final Object[] details;
	
	public Violation(String check, Object...details) {
		this.check = check;
		this.details = details;
	}
	
}
