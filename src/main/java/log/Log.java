package log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {
	private static Logger log;

	/**
	 * This method is used to get/set the specified logger
	 */
	public static void getLogger(String loggerName) {
		log = LogManager.getLogger(loggerName);
	}

	/**
	 * This method will print a trace message to the log
	 * 
	 * @param message - message to be printed in the log
	 */
	public static void trace(String message) {
		log.trace(message);
	}

	/**
	 * This method will print a debug message to the log
	 * 
	 * @param message - message to be printed in the log
	 */
	public static void debug(String message) {
		log.debug(message);
	}

	/**
	 * This method will print an info message to the log
	 * 
	 * @param message - message to be printed in the log
	 */
	public static void info(String message) {
		log.info(message);
	}

	/**
	 * This method will print a warning message to the log
	 * 
	 * @param message - message to be printed in the log
	 */
	public static void warn(String message) {
		log.warn(message);
	}

	/**
	 * This method will print an error message to the log
	 * 
	 * @param message - message to be printed in the log
	 */
	public static void error(String message) {
		log.error(message);
	}

	/**
	 * This method will print a fatal message to the log
	 * 
	 * @param message - message to be printed in the log
	 */
	public static void fatal(String message) {
		log.fatal(message);
	}
}
