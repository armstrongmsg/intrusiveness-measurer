package commons.test;

import org.apache.log4j.PropertyConfigurator;

public class LoggedTest {

	private static final String loggingConfigFile = "conf/test/log4j.conf"; 
	
	{
		PropertyConfigurator.configure(loggingConfigFile);
	}
}
