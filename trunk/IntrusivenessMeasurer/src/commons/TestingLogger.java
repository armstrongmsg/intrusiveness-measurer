package commons;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingLogger {
	
	static final Logger logger = LoggerFactory.getLogger(TestingLogger.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PropertyConfigurator.configure("conf/test/log4j.conf");

		logger.info("info 1 {} {}", 2, 3);
		
		/*
		 Logger logger = Logger.getLogger(TestingLogger.class);*/
		 
		// BasicConfigurator.configure();
		 
//		 Layout layout = new PatternLayout();
		 
//		 Appender appender = new ConsoleAppender(layout, "file");
		 
//		 logger.addAppender(appender);
		/* logger.warn("Low fuel level.");*/
		
		
	}

}
