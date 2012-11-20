package exerciser.internal;

import static commons.util.FileUtil.toPath;
import static commons.util.StringUtil.concat;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import commons.OperatingSystem;
import commons.SystemProcess;
import commons.test.FileBasedTest;

public class MemoryTaskTest extends FileBasedTest {

	private OperatingSystem testSystem;
	private SystemProcess generatedProcess;
	private static final String exerciser = toPath(getTestDataDirectory(), "exerciser");
	private static final String notExistentFile = toPath(getTestDataDirectory(), "notExistentFile");
	private static final String notExecutableFile = toPath(getTestDataDirectory(), "notExecutableFile");
	private static final int amountOfBytesToAllocate = 1000;
	private static final int timeout = 10;
	private static final String expectedCommand = concat(exerciser, " ", String.valueOf(amountOfBytesToAllocate), " ", String.valueOf(timeout));
	
	private MemoryTask task;
	
	@Before
	public void setUp() throws Exception {
		createExerciserFile();
		createNotExecutableFile();
		
		testSystem = createStrictMock(OperatingSystem.class);
		generatedProcess = createStrictMock(SystemProcess.class);
		
		task = new MemoryTask(testSystem, exerciser, amountOfBytesToAllocate, timeout);
	}

	private void createNotExecutableFile() throws IOException {
		File file = new File(notExecutableFile);
		file.createNewFile();
		file.setExecutable(false);
	}
	
	private void createExerciserFile() throws IOException {
		File file = new File(exerciser);
		file.createNewFile();
		file.setExecutable(true);
	}

	@Test(expected = IOException.class)
	public void constructorCannotReceiveNotExistentMemoryExerciserFile() throws IOException {
		task = new MemoryTask(testSystem, notExistentFile, amountOfBytesToAllocate, timeout);
	}
	
	@Test(expected = IOException.class)
	public void constructorCannotReceiveNotExecutableMemoryExerciserFile() throws IOException {
		task = new MemoryTask(testSystem, notExecutableFile, amountOfBytesToAllocate, timeout);
	}
	
	@Test
	public void testRunAndTerminate() throws IOException {
		// starting the process
		expect(testSystem.execute(eq(expectedCommand))).andReturn(generatedProcess);
		
		expectLastCall();
		generatedProcess.execute();
		
		// stopping the process
		expect(testSystem.isRunning(exerciser)).andReturn(true);
		
		expectLastCall();
		generatedProcess.terminate();
		
		replayMocks();
		
		task.run();
		task.terminate();
		
		verifyMocks();
	}

	@Test
	public void testIsRunning() throws IOException {
		// starting the process
		expect(testSystem.execute(eq(expectedCommand))).andReturn(generatedProcess);
		
		expectLastCall();
		generatedProcess.execute();
		
		// getting running information
		expect(testSystem.isRunning(exerciser)).andReturn(true);
		
		// stopping the process
		expect(testSystem.isRunning(exerciser)).andReturn(true);
		
		expectLastCall();
		generatedProcess.terminate();
		
		// getting running information
		expect(testSystem.isRunning(exerciser)).andReturn(false);
		
		replayMocks();
		
		task.run();
		assertTrue(task.isRunning());
		task.terminate();
		assertFalse(task.isRunning());
		
		verifyMocks();
	}

	private void replayMocks() {
		replay(testSystem, generatedProcess);
	}
	
	private void verifyMocks() {
		verify(testSystem, generatedProcess);
	}
}
