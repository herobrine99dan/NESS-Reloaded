package com.github.ness.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

public class LoggerTest {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@Test
	public void executeTest() throws InterruptedException {
		// logger.info("NESS Reloaded is using JavaUtilLogging because it is easy");
		// logger.warning("NESS Reloaded makes a warn message");
		// logger.fine("NESS Reloaded makes a fine message");
		// logger.finest("NESS Reloaded makes a finest message");
		// logger.log(Level.SEVERE, "NESS Reloaded makes an error message");
		// logger.log(Level.WARNING, "NESS Reloaded makes a warning message");
		String number = "A248isTheBest";
		try {
			String[] sentence = new String[] {"A248","Is","The","Best"};
			logger.info(sentence[3]);
			logger.info(sentence[4]);
		} catch (Throwable ex) {
			logger.log(Level.SEVERE, "Integer.valueOf() is sad because it can't convert a string to an integer", ex);
		}
		/*
		 * logger.log(Level.ALL, "NESS Reloaded makes an 'all' message");
		 * logger.log(Level.CONFIG, "NESS Reloaded makes a config message");
		 * logger.log(Level.FINER, "NESS Reloaded makes a finer message");
		 * logger.log(Level.OFF, "NESS Reloaded makes an off message");
		 */
	}
}
