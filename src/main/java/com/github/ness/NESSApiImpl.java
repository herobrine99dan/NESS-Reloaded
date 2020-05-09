package com.github.ness;

import com.github.ness.api.NESSApi;
import com.github.ness.api.ViolationAction;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NESSApiImpl implements NESSApi {

	private final NESSAnticheat ness;
	
	@Override
	public void addViolationAction(ViolationAction action) {
		ness.getViolationManager().addAction(action);
	}

}
