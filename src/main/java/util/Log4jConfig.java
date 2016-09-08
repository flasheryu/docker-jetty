package util;

import java.util.Properties;

public class Log4jConfig extends BaseConfig {
	private static Properties logProperties = new Properties();

	public static void initialize() {
		initialize("log4j.properties", logProperties);
	}

	public static Properties getLogProperties() {
		return logProperties;
	}

	public static void setLogProperties(Properties logProperties) {
		Log4jConfig.logProperties = logProperties;
	}
}
