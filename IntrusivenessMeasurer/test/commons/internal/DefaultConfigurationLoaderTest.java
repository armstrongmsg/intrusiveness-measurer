package commons.internal;

import static commons.util.FileUtil.toPath;
import static commons.util.StringUtil.concat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Before;
import org.junit.Test;

import commons.Configuration;
import commons.exception.NotExistentPropertyException;
import commons.test.FileBasedTest;

public class DefaultConfigurationLoaderTest extends FileBasedTest {

	private static final String dataDirectory = getTestDataDirectory();
	private static final String testConfigurationFileName = toPath(dataDirectory, "conf");
	private static final String propertyNameValueSeparator = "=";
	
	private static final String propertyName1 = "propertyName1";
	private static final String propertyName2 = "propertyName2";
	private static final String propertyName3 = "propertyName3";
	private static final String propertyName4 = "propertyName4";
	private static final String propertyName5 = "propertyName5";
	
	private static final String propertyValue1 = "propertyValue1";
	private static final String propertyValue2 = "_propertyValue2_";
	private static final String propertyValue3 = "propertyValue3";
	private static final String propertyValue4 = "_propertyValue4_";
	
	private DefaultConfigurationLoader loader;
	
	@Before
	public void setUp() throws Exception {
		loader = new DefaultConfigurationLoader();
	}

	@Test
	public void testLoadFromValidConfigurationFile() throws NotExistentPropertyException, IOException {
		createValidConfigurationFile();
		
		Configuration configuration = loader.load(testConfigurationFileName);
		
		assertEquals(propertyValue1, configuration.getProperty(propertyName1));
		assertEquals(propertyValue2, configuration.getProperty(propertyName2));
		assertEquals(propertyValue3, configuration.getProperty(propertyName3));
		assertEquals(propertyValue4, configuration.getProperty(propertyName4));
		assertFalse(configuration.hasProperty(propertyName5));
	}
	
	@Test
	public void testLoadFromConfigurationFileWithCommentedProperties() throws NotExistentPropertyException, IOException {
		createConfigurationFileWithCommentedProperties();
		
		Configuration configuration = loader.load(testConfigurationFileName);
		
		assertEquals(propertyValue2, configuration.getProperty(propertyName2));
		assertEquals(propertyValue3, configuration.getProperty(propertyName3));
		assertEquals(propertyValue4, configuration.getProperty(propertyName4));
		// the commented property should not be considered
		assertFalse(configuration.hasProperty(propertyName1));
		assertFalse(configuration.hasProperty(propertyName5));
	}
	
	@Test
	public void testLoadFromConfigurationFileWithNoProperties() throws NotExistentPropertyException, IOException {
		createConfigurationFileWithNoProperties();
		
		Configuration configuration = loader.load(testConfigurationFileName);
		
		assertFalse(configuration.hasProperty(propertyName1));
		assertFalse(configuration.hasProperty(propertyName2));
		assertFalse(configuration.hasProperty(propertyName3));
		assertFalse(configuration.hasProperty(propertyName4));
		assertFalse(configuration.hasProperty(propertyName5));
	}
	
	@Test(expected = IOException.class)
	public void testLoadFromConfigurationFileWithMissingValue() throws NotExistentPropertyException, IOException {
		createConfigurationFileWithMissingValue();
		loader.load(testConfigurationFileName);
	}
	
	@Test(expected = IOException.class)
	public void testLoadFromConfigurationFileWithMissingName() throws NotExistentPropertyException, IOException {
		createConfigurationFileWithMissingName();
		loader.load(testConfigurationFileName);
	}
	
	@Test(expected = IOException.class)
	public void testLoadFromConfigurationFileWithMissingNameAndValue() throws NotExistentPropertyException, IOException {
		createConfigurationFileWithMissingNameAndValue();
		loader.load(testConfigurationFileName);
	}
	
	private void createValidConfigurationFile() throws IOException {
		File configurationFile = new File(testConfigurationFileName);
		configurationFile.createNewFile();
		RandomAccessFile writer = new RandomAccessFile(configurationFile, "rw");
		writer.writeBytes("# file header\n");
		writer.writeBytes("# file header\n");
		writer.writeBytes("\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName1, propertyNameValueSeparator, propertyValue1, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName2, propertyNameValueSeparator, propertyValue2, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName3, propertyNameValueSeparator, propertyValue3, "\n"));
		writer.writeBytes(concat(propertyName4, propertyNameValueSeparator, propertyValue4, "\n"));
		writer.close();
	}
	
	private void createConfigurationFileWithCommentedProperties() throws IOException {
		File configurationFile = new File(testConfigurationFileName);
		configurationFile.createNewFile();
		RandomAccessFile writer = new RandomAccessFile(configurationFile, "rw");
		writer.writeBytes("# file header\n");
		writer.writeBytes("# file header\n");
		writer.writeBytes("\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat("# ", propertyName1, propertyNameValueSeparator, propertyValue1, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName2, propertyNameValueSeparator, propertyValue2, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName3, propertyNameValueSeparator, propertyValue3, "\n"));
		writer.writeBytes(concat(propertyName4, propertyNameValueSeparator, propertyValue4, "\n"));
		writer.close();
	}

	private void createConfigurationFileWithNoProperties() throws IOException {
		File configurationFile = new File(testConfigurationFileName);
		configurationFile.createNewFile();
		RandomAccessFile writer = new RandomAccessFile(configurationFile, "rw");
		writer.writeBytes("# file header\n");
		writer.writeBytes("# file header\n");
		writer.writeBytes("\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.close();
	}
	

	private void createConfigurationFileWithMissingValue() throws IOException {
		File configurationFile = new File(testConfigurationFileName);
		configurationFile.createNewFile();
		RandomAccessFile writer = new RandomAccessFile(configurationFile, "rw");
		writer.writeBytes("# file header\n");
		writer.writeBytes("# file header\n");
		writer.writeBytes("\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName1, propertyNameValueSeparator, propertyValue1, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName2, propertyNameValueSeparator, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName3, propertyNameValueSeparator, propertyValue3, "\n"));
		writer.writeBytes(concat(propertyName4, propertyNameValueSeparator, propertyValue4, "\n"));
		writer.close();
	}
	
	private void createConfigurationFileWithMissingName() throws IOException {
		File configurationFile = new File(testConfigurationFileName);
		configurationFile.createNewFile();
		RandomAccessFile writer = new RandomAccessFile(configurationFile, "rw");
		writer.writeBytes("# file header\n");
		writer.writeBytes("# file header\n");
		writer.writeBytes("\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName1, propertyNameValueSeparator, propertyValue1, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName2, propertyNameValueSeparator, propertyValue2, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyNameValueSeparator, propertyValue3, "\n"));
		writer.writeBytes(concat(propertyName4, propertyNameValueSeparator, propertyValue4, "\n"));
		writer.close();
	}
	
	private void createConfigurationFileWithMissingNameAndValue() throws IOException {
		File configurationFile = new File(testConfigurationFileName);
		configurationFile.createNewFile();
		RandomAccessFile writer = new RandomAccessFile(configurationFile, "rw");
		writer.writeBytes("# file header\n");
		writer.writeBytes("# file header\n");
		writer.writeBytes("\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName1, propertyNameValueSeparator, propertyValue1, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyNameValueSeparator, "\n"));
		writer.writeBytes("# some comments\n");
		writer.writeBytes("# some comments\n");
		writer.writeBytes(concat(propertyName3, propertyNameValueSeparator, propertyValue3, "\n"));
		writer.writeBytes(concat(propertyName4, propertyNameValueSeparator, propertyValue4, "\n"));
		writer.close();
	}
}
