package usermonitor.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import usermonitor.MemoryInfo;

public class DefaultUserMonitorTest {

	{
		PropertyConfigurator.configure("conf/test/log4j.conf");
	}
	
	private final double testDeltaError = 0.005;
	private final String testMemoryFileName = "memory";
	private final String testCPUFileName = "cpu";
	private final double testTotalMemory = 1000;
	private final double testUsedMemory = 600;
	
	private final int performanceTestNumberOfRepetitions = 1000;
	private final double performanceTestMemoryLimitTime = 30;
			
	private DefaultUserMonitor monitor;
	
	@Before
	public void setUp() throws IOException {
		new File(testMemoryFileName).createNewFile();
		new File(testCPUFileName).createNewFile();
		
		monitor = new DefaultUserMonitor(testMemoryFileName, testCPUFileName);
	}
	
	@After
	public void tearDown() {
		new File(testMemoryFileName).delete();
		new File(testCPUFileName).delete();
	}
	
	@Test
	public void testGetMemoryUsage() throws IOException {
		writeValidMemoryFile();
		MemoryInfo result = monitor.getMemoryUsage();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
	}
	
	@Test
	public void testGetMemoryUsageFromFileWithInvertedOrderedData() throws IOException {
		writeMemoryFileWithInvertedOrderedData();
		MemoryInfo result = monitor.getMemoryUsage();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
	}
	
	@Test
	public void testGetMemoryUsageFromFileWithCommentedLines() throws IOException {
		writeMemoryFileCommentedLines();
		MemoryInfo result = monitor.getMemoryUsage();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
	}
	
	@Test(expected = IOException.class)
	public void testGetMemoryUsageFromIncompleteFile() throws IOException {
		writeMemoryFileWithMissingInformation();
		monitor.getMemoryUsage();
	}
	
	@Test(expected = IOException.class)
	public void testGetMemoryUsageFromInvalidFormatFile() throws IOException {
		writeMemoryFileWithInvalidFormat();
		monitor.getMemoryUsage();
	}
	
	@Test(expected = IOException.class)
	public void testGetMemoryUsageFromEmptyFile() throws IOException {
		monitor.getMemoryUsage();
	}
	
	@Test
	public void testGetMemoryPerformanceTest() throws IOException {
		writeValidMemoryFile();
		long timeStart = System.currentTimeMillis();
		
		for (int i = 0; i < performanceTestNumberOfRepetitions; i++) {
			monitor.getMemoryUsage();
		}
		
		double delta = System.currentTimeMillis() - timeStart;
		assertTrue(delta/performanceTestNumberOfRepetitions < performanceTestMemoryLimitTime);
	}
	

	private void writeValidMemoryFile() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + "\n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + "\n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileWithMissingInformation() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + "\n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileWithInvalidFormat() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write((testTotalMemory + "\n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + "\n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileCommentedLines() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write("# comment\n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + "\n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write("# comment\n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + "\n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileWithInvertedOrderedData() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + "\n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + "\n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
}
