package commons;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

public interface DataBase<K extends Serializable, V extends Serializable> {
	void save(Map<K, V> map) throws IOException;
	Map<K, V> load() throws IOException;
	void close() throws IOException;
	String getName();
}
