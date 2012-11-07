package commons.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import commons.test.FileBasedTest;

public class DefaultDataBaseTest extends FileBasedTest {

	private static final String dataBaseName = "db";
	@SuppressWarnings("static-access")
	private final String dataDirectory = super.testDataDirectory;
	private DefaultDataBase<String, Integer> dataBase;
	private Map<String, Integer> mapToSave; 
	private Map<String, Integer> loadedMap; 
	
	@Before
	public void startUp() throws IOException {
		mapToSave = new HashMap<String, Integer>();
		dataBase = new DefaultDataBase<String, Integer>(dataBaseName, dataDirectory);
	}
	
//	@Test
	public void testLoadAndSavingMaps0() throws IOException {
		mapToSave.put("key0", 0);
		dataBase.save(mapToSave);
		dataBase.close();
	}
	
	@Test
	public void testLoadAndSavingMaps1() throws IOException {
//		mapToSave.put("key0", 0);
//		dataBase.save(mapToSave);
		Map<String, Integer> foo = dataBase.load();
		for (Entry<String, Integer> iterable_element : foo.entrySet()) {
			System.out.println(iterable_element.getKey());
			System.out.println(iterable_element.getValue());
		}
	}
	
//	@Test
	public void testLoadAndSavingMaps() throws IOException {
		dataBase.save(mapToSave);
		loadedMap = dataBase.load();
		assertEquals(mapToSave, loadedMap);
			
		mapToSave.put("key0", 0);
		dataBase.save(mapToSave);
		loadedMap = dataBase.load();
		assertEquals(mapToSave, loadedMap);
		
		mapToSave.put("key1", 1);
		dataBase.save(mapToSave);
		loadedMap = dataBase.load();
		assertEquals(mapToSave, loadedMap);
		
		mapToSave.remove("key1");
		dataBase.save(mapToSave);
		loadedMap = dataBase.load();
		assertEquals(mapToSave, loadedMap);
		
		mapToSave.remove("key0");
		dataBase.save(mapToSave);
		loadedMap = dataBase.load();
		assertEquals(mapToSave, loadedMap);
		
		mapToSave.put("key0", 0);
		dataBase.save(mapToSave);
		loadedMap = dataBase.load();
		assertEquals(mapToSave, loadedMap);
		
		mapToSave.put("key1", 1);
		dataBase.save(mapToSave);
		loadedMap = dataBase.load();
		assertEquals(mapToSave, loadedMap);
		
		dataBase.close();
		
		
	}
}
