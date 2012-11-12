package commons;

import java.io.IOException;

/**
 * A class which is able to create Configuration instances 
 * from information stored in files.
 * 
 * @author Armstrong Mardilson da Silva Goes
 *
 */
public interface ConfigurationLoader {
	/**
	 * Create a Configuration instance from the given file.
	 * file must not be null and must be readable.
	 * 
	 * @param file The file from which the data will be read.
	 * @return The created Configuration instance.
	 * @throws IOException If occurs an error while reading the file 
	 * data, or when opening or closing the file or if any of the 
	 * underlying classes used to access the file throws an exception, 
	 * or if the file is not readable.
	 * @throws IllegalArgumentException If file is null.
	 */
	Configuration load(String file) throws IOException;
}
