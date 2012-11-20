package commons.internal;

import static commons.Preconditions.checkNotNull;
import static commons.util.FileUtil.checkFileExist;
import static commons.util.FileUtil.checkFileIsReadable;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

import commons.Configuration;
import commons.ConfigurationLoader;

/**
 * An implementation of ConfigurationLoader which uses 
 * {@link RandomAccessFile} to read the configuration files.
 * The file's properties names and values must be written in the same line 
 * and separated by PROPERTY_NAME_VALUE_SEPARATOR. Commentaries must start 
 * with COMMENTED_LINE_HEADER. This class accepts configuration files with 
 * blank lines.
 * 
 * Usage example:</br>
 * </br>
 * COMMENTED_LINE_HEADER comments</br>
 * blank line</br>
 * name PROPERTY_NAME_VALUE_SEPARATOR value</br>
 * </br>
 * 
 * To pass a file which does not respect the rules above will cause the load 
 * method to throw IOException.
 * 
 * @author Armstrong Mardilson da Silva Goes
 */
public class DefaultConfigurationLoader implements ConfigurationLoader {

	private static final String COMMENTED_LINE_HEADER = "#";
	private static final String PROPERTY_NAME_VALUE_SEPARATOR = "=";

	@Override
	public Configuration load(String file) throws IOException {
		checkNotNull(file, "file must not be null.");
		checkFileExist(file);
		checkFileIsReadable(file);
		
		RandomAccessFile configurationFile = new RandomAccessFile(file, "r");
		return new DefaultConfiguration(readProperties(configurationFile));
	}

	private Properties readProperties(RandomAccessFile configurationFile) throws IOException {
		Properties properties = new Properties(); 
		while (thereIsPropertyToRead(configurationFile)) {
			String[] property = readProperty(configurationFile);
			properties.put(property[0], property[1]);
		}
		return properties;
	}
	
	private String[] readProperty(RandomAccessFile configurationFile) throws IOException {
		String[] property = configurationFile.readLine().split(PROPERTY_NAME_VALUE_SEPARATOR);
		if (property.length != 2 || property[0].isEmpty() || property[1].isEmpty()) {
			throw new IOException("Invalid configuration file format.");
		}
		return property;
	}

	private boolean thereIsPropertyToRead(RandomAccessFile configurationFile) throws IOException {
		String nextLine = configurationFile.readLine();
		while (nextLine != null) {
			if (isPropertyLine(nextLine)) {
				// resets the file to the position before checking the existence of a property line
				configurationFile.seek(configurationFile.getFilePointer() - nextLine.length() - 1);
				return true;
			}
			nextLine = configurationFile.readLine();
		}
		return false;
	}

	private boolean isPropertyLine(String nextLine) {
		return !nextLine.startsWith(COMMENTED_LINE_HEADER) && !nextLine.isEmpty();
	}
}
