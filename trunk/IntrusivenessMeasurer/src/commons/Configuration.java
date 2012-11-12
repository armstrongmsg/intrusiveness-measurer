package commons;

import commons.exception.NotExistentPropertyException;

/**
 * Represents a basic configuration, which can store 
 * some properties and provide other classes with them.
 * Note this class is not thread-safe.
 * 
 * @author Armstrong Mardilson da Silva Goes
 */
public interface Configuration {
	/**
	 * Returns the value of the given property's name. 
	 * 
	 * @param name The property's name. It must not be null.
	 * @return The value of the given property's name .
	 * @throws NotExistentPropertyException If the given property 
	 * does not exist.
	 * @throws IllegalArgumentException If name is null.
	 */
	String getProperty(String name) throws NotExistentPropertyException;
	
	/**
	 * Returns true if the given property exists.
	 * 
	 * @param name The property's name.
	 */
	boolean hasProperty(String name);
}
