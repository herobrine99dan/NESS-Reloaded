package com.github.ness;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

public class StackTraceTest {

	@Test
	public void testStacktrace() {
		Logger.getLogger(getClass().getName()).log(Level.INFO, "you should see stacktrace, param1 = {0}", new Object[] {"param1", new Throwable()});
	}
	
}
