package commons;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

public interface MapFactory extends Closeable {
	// FIXME these constants should not be here
	String METADATA_DIRECTORY = "metadata_directory";
	String METADATA_FILENAME = "metadata_filename";
	void configure(Properties properties) throws IOException;
	<K extends Serializable, V extends Serializable> Map<K, V>
						createMap(String name) throws IOException;
}
