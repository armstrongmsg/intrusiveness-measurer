package commons.internal;

import static commons.Preconditions.checkNotNull;

import java.util.Properties;

import commons.Configuration;
import commons.exception.NotExistentPropertyException;

/**
 * This implementation of {@link Configuration} uses 
 * a {@link Properties} instance to store its properties.
 * 
 * @author Armstrong Mardilson da Silva Goes
 */
public class DefaultConfiguration implements Configuration {

	private final Properties properties;
	
	/**
	 * Constructs a new DefaultConfiguration instance, whose properties
	 * will be the ones stored in the given {@link Properties}
	 * instance.
	 * 
	 * @throws IllegalArgumentException If properties is null.
	 */
	public DefaultConfiguration(Properties properties) {
		checkNotNull(properties, "properties must not be null.");
		this.properties = properties;
	}
	
	@Override
	public String getProperty(String name) throws NotExistentPropertyException {
		if (!properties.containsKey(name)) {
			throw new NotExistentPropertyException("There is no property called " + name);
		}
		return properties.getProperty(name);
	}

	@Override
	public boolean hasProperty(String name) {
		return properties.containsKey(name);
	}
}
