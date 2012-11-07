package commons.internal;

import static commons.FileUtil.checkFileIsReadable;
import static commons.FileUtil.checkFileIsWritable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Map;

import commons.DataBase;

public class DefaultDataBase<K extends Serializable,V extends Serializable> implements DataBase<K,V> {

	private String name;
	private RandomAccessFile db;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	// FIXME today i cannot use two dbs in the same file
	// FIXME now the database cannot be recreated from the db file
	public DefaultDataBase(String name, String dataDirectory) throws IOException {
		this.name = name;
		String dbPath = dataDirectory + File.separator + name;
		File dbFile = new File(dbPath);
		
		if (!dbFile.exists()) {
			dbFile.createNewFile();
		}
		checkFileIsReadable(dbPath);
		checkFileIsWritable(dbPath);
		output = new ObjectOutputStream(new FileOutputStream(dbFile));
		input = new ObjectInputStream(new FileInputStream(dbFile));
		db = new RandomAccessFile(dbFile, "rw");
	}
	
	@Override
	public void save(Map<K,V> map) throws IOException {
		db.setLength(0);
		// FIXME investigate why without this line 
		// the saving does not work
		output.reset();
		output.writeObject(map);
		output.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<K,V> load() throws IOException {
		Map<K,V> loaded = null;
		try {
			loaded = (Map<K,V>) input.readObject();	
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
		return loaded;
	}

	@Override
	public void close() throws IOException {
		input.close();
		output.flush();
		output.close();
		db.close();
	}

	@Override
	public String getName() {
		return name;
	}
}
