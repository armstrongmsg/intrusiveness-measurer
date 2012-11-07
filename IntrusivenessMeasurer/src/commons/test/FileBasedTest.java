package commons.test;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;

public class FileBasedTest extends LoggedTest {

	protected final static String testDataDirectory = "target"; 
	
	@Before
	public void setUp() throws IOException {
		new File(testDataDirectory).mkdirs();
	}
	
	@After
	public void tearDown() throws IOException {
		//FileUtils.deleteDirectory(testDataDirectory);
	}
}
