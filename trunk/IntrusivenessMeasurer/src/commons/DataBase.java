package commons;

public interface DataBase {
	void save(Object key, Object value);
	void load(Object key);
}
