package commons;

import static commons.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Armstrong Mardilson da Silva Goes
 *
 * @param <K>
 * @param <V>
 */
public class PersistentMap<K extends Serializable, V extends Serializable> 
							implements Map<K, V> {

	private static final Logger logger = LoggerFactory.getLogger(PersistentMap.class);
	private final DataBase<K,V> db;
	private final Map<K,V> dataMap;
	
	public PersistentMap(DataBase<K,V> db) throws IOException {
		checkNotNull(db, "db must not be null.");
		this.db = db;
		dataMap = (Map<K, V>) db.load();
	}
	
	@Override
	public int size() {
		return dataMap.size();
	}

	@Override
	public boolean isEmpty() {
		return dataMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return dataMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return dataMap.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return dataMap.get(key);
	}

	@Override
	public V put(K key, V value) {
		V oldValue = dataMap.put(key, value);
		try {
			db.save(dataMap);
		} catch (IOException e) {
			logger.info("Could not save data in database.", e);
		}
		return oldValue;
	}

	@Override
	public V remove(Object key) {
		V oldValue = dataMap.remove(key);
		try {
			db.save(dataMap);
		} catch (IOException e) {
			logger.info("Could not save data in database.", e);
		}
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		dataMap.putAll(m);
		try {
			db.save(dataMap);
		} catch (IOException e) {
			logger.info("Could not save data in database.", e);
		}
	}

	@Override
	public void clear() {
		dataMap.clear();
		try {
			db.save(dataMap);
		} catch (IOException e) {
			logger.info("Could not save data in database.", e);
		}
	}

	@Override
	public Set<K> keySet() {
		return dataMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return dataMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return dataMap.entrySet();
	}
}
