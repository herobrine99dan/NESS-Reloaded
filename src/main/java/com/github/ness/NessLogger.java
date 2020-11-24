package com.github.ness;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class only exists because NESS is stuck using java.util.logging for
 * compatibility purposes
 *
 */
public final class NessLogger {

    private NessLogger() {
    }

    /**
     * Gets a logger for the specified class
     * 
     * @param clazz the class
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());

        /*
         * There are 2 requirements for a JUL logger to send output: 1. setLevel is
         * called 2. log4j2.xml is configured to allow it
         * 
         * This call takes care of the first requirement
         */
        logger.setLevel(Level.ALL);

        return logger;
    }

}
