package commons.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.ufcg.lsd.beefs.commons.persistence.MapFactory;

import commons.test.FileBasedTest;

// this test is Serializable because there is an inner class which must be Serializable too. 
public class PersistentMapFactoryTest extends FileBasedTest implements Serializable {

	private static final long serialVersionUID = -7206027709105576124L;
	private static final String testDataDirectory = FileBasedTest.getTestDataDirectory();
	private static final String metadataFileName = testDataDirectory + File.separator + "metadata";
	private static final String testMapName1 = "map1";
	private static final String testMapName2 = "map2";
	private static final int numberOfRepetitions = 10;
	
	private transient PersistentMapFactory mapFactory;
	
	@Before
	public void setUp() throws Exception {
		mapFactory = new PersistentMapFactory();
		Properties properties = new Properties();
		properties.put(MapFactory.METADATA_DIRECTORY, testDataDirectory);
		properties.put(MapFactory.METADATA_FILENAME, metadataFileName);
		mapFactory.configure(properties);
	}

	@After
	public void tearDown() throws IOException {
		mapFactory.close();
	}
	
	@Test
	public void testPutAndGet() throws IOException {
		Map<String, Integer> map = mapFactory.createMap(testMapName1);
		
		for (Integer i = 0; i < numberOfRepetitions; i++) {
			map.put(i.toString(), i);
		}
		
		for (Integer i = 0; i < numberOfRepetitions; i++) {
			assertEquals(i, map.get(i.toString()));
		}
	}
	
	@Test
	public void usingOtherMapToGetInformation() throws IOException {
		Map<String, Integer> map1 = mapFactory.createMap(testMapName1);
		
		for (Integer i = 0; i < numberOfRepetitions; i++) {
			map1.put(i.toString(), i);
		}
		
		Map<String, Integer> map2 = mapFactory.createMap(testMapName1);
		
		for (Integer i = 0; i < numberOfRepetitions; i++) {
			assertEquals(i, map2.get(i.toString()));
		}
	}
	
	@Test
	public void testRemove() throws IOException {
		Map<String, Integer> map = mapFactory.createMap(testMapName1);
		
		for (Integer i = 0; i < numberOfRepetitions; i++) {
			map.put(i.toString(), i);
		}
		
		for (Integer i = 0; i < numberOfRepetitions; i++) {
			assertEquals(i, map.get(i.toString()));
		}
		
		Map<String, Integer> map2 = mapFactory.createMap(testMapName1);
		
		for (Integer i = 0; i < numberOfRepetitions; i++) {
			assertEquals(i, map2.get(i.toString()));
		}
		
		map.remove("0");
		
		Map<String, Integer> map3 = mapFactory.createMap(testMapName1);
		assertFalse(map3.containsKey("0"));
		
		for (Integer i = 1; i < numberOfRepetitions; i++) {
			assertEquals(i, map2.get(i.toString()));
		}
	}
	
	@Test
	public void persistenceOfComplexObjects() throws IOException {
		ObjectTest objectTest = new ObjectTest("aaa", 10);
		Map<Integer, ObjectTest> map = mapFactory.createMap(testMapName2);
		
		map.put(0, objectTest);
		ObjectTest object = map.get(0);
		
		assertEquals(objectTest.fieldString, object.fieldString);
		assertEquals(objectTest.fieldInteger, object.fieldInteger);
		assertEquals(objectTest.fieldMap, object.fieldMap);
	}
	
	private class ObjectTest implements Serializable {
		private static final long serialVersionUID = 4412696987679672521L;
		public String fieldString;
		public Integer fieldInteger;
		public Map<String, Integer> fieldMap;
		
		public ObjectTest(String fieldString, Integer fieldInteger) {
			this.fieldString = fieldString;
			this.fieldInteger = fieldInteger;
			fieldMap = new HashMap<String, Integer>();
			fieldMap.put(fieldString, fieldInteger);
		}
	}
}
