package com.github.ness.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

public class LoggerTest {

	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	@Test
	public void executeTest() throws InterruptedException {
		logger.info("NESS Reloaded is using JavaUtilLogging because it is easy");
		logger.warning("NESS Reloaded makes a warn message");
		logger.fine("NESS Reloaded makes a fine message");
		logger.finest("NESS Reloaded makes a finest message");
		logger.log(Level.SEVERE, "NESS Reloaded makes an error message");
		logger.log(Level.WARNING, "NESS Reloaded makes a warning message");
		logger.log(Level.ALL, "NESS Reloaded makes an 'all' message");
		logger.log(Level.CONFIG, "NESS Reloaded makes a config message");
		logger.log(Level.FINER, "NESS Reloaded makes a finer message");
		logger.log(Level.OFF, "NESS Reloaded makes an off message");
	}
}
