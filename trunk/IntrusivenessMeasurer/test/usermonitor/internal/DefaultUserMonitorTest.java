package usermonitor.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Before;
import org.junit.Test;

import usermonitor.CPUConfiguration;
import usermonitor.CPUInfo;
import usermonitor.MemoryInfo;

import commons.test.FileBasedTest;

public class DefaultUserMonitorTest extends FileBasedTest {
	
	private final double testDeltaError = 0.005;
	
	@SuppressWarnings("static-access")
	private final String dataDirectory = super.testDataDirectory;
	private final String testMemoryFileName = dataDirectory + File.separator + "memory";
	private final String testCPUInfoFileName = dataDirectory + File.separator + "cpuInfo";
	private final String testCPUUsageFileName = dataDirectory + File.separator + "cpuUsage";
	private final String testMemoryRealFileName = "/proc/meminfo";
	private final String testCPUInfoRealFileName = "/proc/cpuinfo";
	private final String testCPUUsageRealFileName = testCPUUsageFileName;

	private final String testCPU1ModelName = "model name 1";
	private final String testCPU2ModelName = "model name 2";
	private final String testCPU3ModelName = testCPU2ModelName;
	
	private final double cpu1Frequency = 1000;
	private final double cpu2Frequency = 456;
	private final double cpu3Frequency = 456;
	
	private final double cpu1CacheSize = 555;
	private final double cpu2CacheSize = 123;
	private final double cpu3CacheSize = 123;
	
	private final double cpuUserUsage = 2.1;
	private final double cpuIdle = 55.01;
	private final double cpuSystemUsage = 12.99;
	
	private final double testTotalMemory = 1000;
	private final double testUsedMemory = 600;
	
	private final int performanceTestNumberOfRepetitions = 100;
	private final double performanceTestMemoryLimitTime = 50;
	private final double performanceTestCPULimitTime = 100;		
	
	private DefaultUserMonitor monitor;
	
	@Before
	public void startUp() throws IOException {
		new File(testMemoryFileName).createNewFile();
		new File(testCPUInfoFileName).createNewFile();
		new File(testCPUUsageFileName).createNewFile();
		
		monitor = new DefaultUserMonitor(testMemoryFileName, testCPUInfoFileName, testCPUUsageFileName);
	}
	/*
	@After
	public void shutDown() {
		new File(testMemoryFileName).delete();
		new File(testCPUInfoFileName).delete();
		new File(testCPUUsageFileName).delete();
	}*/
	
	
	/*
	 * Constructor tests
	 */
	
	@Test(expected = FileNotFoundException.class)
	public void testConstructorMustReceiveExistentMemoryInfoFileName() throws IOException {
		new DefaultUserMonitor("non-existent file", testCPUInfoFileName, testCPUUsageFileName);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testConstructorMustReceiveExistentCPUInfoFileName() throws IOException {
		new DefaultUserMonitor(testMemoryFileName, "non-existent file", testCPUUsageFileName);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testConstructorMustReceiveExistentCPUUsageFileName() throws IOException {
		new DefaultUserMonitor(testMemoryFileName, testCPUInfoFileName, "non-existent file");
	}
	
	
	/*
	 * Memory Info tests
	 */
	
	@Test
	public void testGetMemoryUsage() throws IOException {
		writeValidMemoryFile();
		MemoryInfo result = monitor.getMemoryInfo();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
	}
	
	@Test
	public void testGetMemoryUsageFromFileWithInvertedOrderedData() throws IOException {
		writeMemoryFileWithInvertedOrderedData();
		MemoryInfo result = monitor.getMemoryInfo();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
	}
	
	@Test
	public void testGetMemoryUsageFromFileWithCommentedLines() throws IOException {
		writeMemoryFileCommentedLines();
		MemoryInfo result = monitor.getMemoryInfo();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
	}
	
	@Test
	public void testGetMemoryResetsMemoryFiles() throws IOException {
		writeValidMemoryFile();
		
		// first call
		MemoryInfo result = monitor.getMemoryInfo();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
		
		// second call
		result = monitor.getMemoryInfo();
		assertEquals(testTotalMemory, result.getTotalMemory(), testDeltaError);
		assertEquals(testUsedMemory, result.getUsedMemory(), testDeltaError);
	}
	
	@Test(expected = IOException.class)
	public void testGetMemoryUsageFromIncompleteFile() throws IOException {
		writeMemoryFileWithMissingInformation();
		monitor.getMemoryInfo();
	}
	
	@Test(expected = IOException.class)
	public void testGetMemoryUsageFromInvalidFormatFile() throws IOException {
		writeMemoryFileWithInvalidFormat();
		monitor.getMemoryInfo();
	}
	
	@Test(expected = IOException.class)
	public void testGetMemoryUsageFromEmptyFile() throws IOException {
		monitor.getMemoryInfo();
	}
	
	@Test
	public void testGetMemoryPerformanceTest() throws IOException {
		writeValidMemoryFile();
		long timeStart = System.currentTimeMillis();
		
		for (int i = 0; i < performanceTestNumberOfRepetitions; i++) {
			monitor.getMemoryInfo();
		}
		
		double delta = System.currentTimeMillis() - timeStart;
		assertTrue(delta/performanceTestNumberOfRepetitions < performanceTestMemoryLimitTime);
	}
	
	@Test
	public void testGetMemoryFromRealFilePerformanceTest() throws IOException {
		monitor = new DefaultUserMonitor(testMemoryRealFileName, testCPUInfoFileName, testCPUUsageFileName);
		long timeStart = System.currentTimeMillis();
		
		for (int i = 0; i < performanceTestNumberOfRepetitions; i++) {
			monitor.getMemoryInfo();
		}
		
		double delta = System.currentTimeMillis() - timeStart;
		assertTrue(delta/performanceTestNumberOfRepetitions < performanceTestMemoryLimitTime);
	}
	
	/*
	 * 
	 * CPU Info tests
	 * 
	 */
	
	@Test
	public void testGetCPUInfoTest() throws IOException {
		writeValidCPUInfoFile();
		writeValidCPUUsageFile();
		
		CPUInfo result = monitor.getCPUInfo();
		
		assertEquals(3, result.getCpus().size());
		
		CPUConfiguration cpu1 = result.getCpus().get(0);
		assertEquals(testCPU1ModelName, cpu1.getModelName());
		assertEquals(cpu1CacheSize, cpu1.getCacheSize(), testDeltaError);
		assertEquals(cpu1Frequency, cpu1.getCpuFrequency(), testDeltaError);
		
		CPUConfiguration cpu2 = result.getCpus().get(1);
		assertEquals(testCPU2ModelName, cpu2.getModelName());
		assertEquals(cpu2CacheSize, cpu2.getCacheSize(), testDeltaError);
		assertEquals(cpu2Frequency, cpu2.getCpuFrequency(), testDeltaError);
		
		CPUConfiguration cpu3 = result.getCpus().get(2);
		assertEquals(testCPU3ModelName, cpu3.getModelName());
		assertEquals(cpu3CacheSize, cpu3.getCacheSize(), testDeltaError);
		assertEquals(cpu3Frequency, cpu3.getCpuFrequency(), testDeltaError);
		
		assertEquals(cpuIdle, result.getIdle(), testDeltaError);
		assertEquals(cpuUserUsage, result.getUserUsage(), testDeltaError);	
		assertEquals(cpuSystemUsage, result.getSystemUsage(), testDeltaError);	
	}
	
	@Test
	public void testGetCPUInfoResetsCPUFiles() throws IOException {
		writeValidCPUInfoFile();
		writeValidCPUUsageFile();
		
		// first call
		CPUInfo result = monitor.getCPUInfo();
		
		assertEquals(3, result.getCpus().size());
		
		CPUConfiguration cpu1 = result.getCpus().get(0);
		assertEquals(testCPU1ModelName, cpu1.getModelName());
		assertEquals(cpu1CacheSize, cpu1.getCacheSize(), testDeltaError);
		assertEquals(cpu1Frequency, cpu1.getCpuFrequency(), testDeltaError);
		
		CPUConfiguration cpu2 = result.getCpus().get(1);
		assertEquals(testCPU2ModelName, cpu2.getModelName());
		assertEquals(cpu2CacheSize, cpu2.getCacheSize(), testDeltaError);
		assertEquals(cpu2Frequency, cpu2.getCpuFrequency(), testDeltaError);
		
		CPUConfiguration cpu3 = result.getCpus().get(2);
		assertEquals(testCPU3ModelName, cpu3.getModelName());
		assertEquals(cpu3CacheSize, cpu3.getCacheSize(), testDeltaError);
		assertEquals(cpu3Frequency, cpu3.getCpuFrequency(), testDeltaError);
		
		assertEquals(cpuIdle, result.getIdle(), testDeltaError);
		assertEquals(cpuUserUsage, result.getUserUsage(), testDeltaError);	
		assertEquals(cpuSystemUsage, result.getSystemUsage(), testDeltaError);
		
		// second call
		result = monitor.getCPUInfo();
		
		assertEquals(3, result.getCpus().size());
		
		cpu1 = result.getCpus().get(0);
		assertEquals(testCPU1ModelName, cpu1.getModelName());
		assertEquals(cpu1CacheSize, cpu1.getCacheSize(), testDeltaError);
		assertEquals(cpu1Frequency, cpu1.getCpuFrequency(), testDeltaError);
		
		cpu2 = result.getCpus().get(1);
		assertEquals(testCPU2ModelName, cpu2.getModelName());
		assertEquals(cpu2CacheSize, cpu2.getCacheSize(), testDeltaError);
		assertEquals(cpu2Frequency, cpu2.getCpuFrequency(), testDeltaError);
		
		cpu3 = result.getCpus().get(2);
		assertEquals(testCPU3ModelName, cpu3.getModelName());
		assertEquals(cpu3CacheSize, cpu3.getCacheSize(), testDeltaError);
		assertEquals(cpu3Frequency, cpu3.getCpuFrequency(), testDeltaError);
		
		assertEquals(cpuIdle, result.getIdle(), testDeltaError);
		assertEquals(cpuUserUsage, result.getUserUsage(), testDeltaError);	
		assertEquals(cpuSystemUsage, result.getSystemUsage(), testDeltaError);
	}
	
	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithMissingModelName() throws IOException {
		writeCPUInfoFileWithMissingModelName();
		writeValidCPUUsageFile();
		monitor.getCPUInfo();
	}
	
	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithMissingCPUFrequency() throws IOException {
		writeCPUInfoFileWithMissingCPUFrequency();
		writeValidCPUUsageFile();
		monitor.getCPUInfo();
	}
	
	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithMissingCacheSize() throws IOException {
		writeCPUInfoFileWithMissingCacheSize();
		writeValidCPUUsageFile();
		monitor.getCPUInfo();
	}

	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithMissingUserUsage() throws IOException {
		writeValidCPUInfoFile();
		writeCPUUsageFileWithMissingUserUsage();
		monitor.getCPUInfo();
	}

	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithMissingSystemUsage() throws IOException {
		writeValidCPUInfoFile();
		writeCPUUsageFileWithMissingSystemUsage();
		monitor.getCPUInfo();
	}

	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithMissingIdle() throws IOException {
		writeValidCPUInfoFile();
		writeCPUUsageFileWithMissingIdle();
		monitor.getCPUInfo();
	}

	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithNotNumericUserUsage() throws IOException {
		writeValidCPUInfoFile();
		writeCPUUsageWithNotNumericUserUsage();
		monitor.getCPUInfo();
	}
	
	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithNotNumericSystemUsage() throws IOException {
		writeValidCPUInfoFile();
		writeCPUUsageWithNotNumericSystemUsage();
		monitor.getCPUInfo();
	}

	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithNotNumericIdle() throws IOException {
		writeValidCPUInfoFile();
		writeCPUUsageWithNotNumericIdle();
		monitor.getCPUInfo();
	}

	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithNotNumericCPUFrequency() throws IOException {
		writeCPUInfoFileWithNotNumericCPUFrequency();
		writeValidCPUUsageFile();
		monitor.getCPUInfo();
	}

	@Test(expected = IOException.class)
	public void testGetCPUInfoFromFileWithNotNumericCacheSize() throws IOException {
		writeCPUInfoFileWithNotNumericCacheSize();
		writeValidCPUUsageFile();
		monitor.getCPUInfo();
	}	

	@Test
	public void testGetCPUInfoPerformanceTest() throws IOException {
		writeValidCPUInfoFile();
		writeValidCPUUsageFile();
		long timeStart = System.currentTimeMillis();
		
		for (int i = 0; i < performanceTestNumberOfRepetitions; i++) {
			monitor.getCPUInfo();
		}
		
		double delta = System.currentTimeMillis() - timeStart;
		assertTrue(delta/performanceTestNumberOfRepetitions < performanceTestCPULimitTime);
	}
	
	@Test
	public void testGetCPUInfoFromRealFilePerformanceTest() throws IOException {
		writeValidCPUInfoFile();
		writeValidCPUUsageFile();
		monitor = new DefaultUserMonitor(testMemoryRealFileName, testCPUInfoRealFileName, testCPUUsageRealFileName);
		long timeStart = System.currentTimeMillis();
		
		for (int i = 0; i < performanceTestNumberOfRepetitions; i++) {
			monitor.getCPUInfo();
		}
		
		double delta = System.currentTimeMillis() - timeStart;
		assertTrue(delta/performanceTestNumberOfRepetitions < performanceTestCPULimitTime);
	}
	
	
	/*
	 * Util functions
	 * TODO investigate if some code can be transferred to other class.
	 */
	
	/*
	 * Memory Util
	 */
	
	private void writeValidMemoryFile() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + " kB\n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + " kB\n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileWithMissingInformation() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + " kB \n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileWithInvalidFormat() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write((testTotalMemory + " kB \n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + " kB \n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileCommentedLines() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write("# comment\n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + " kB \n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write("# comment\n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + " kB \n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	private void writeMemoryFileWithInvertedOrderedData() throws IOException {
		RandomAccessFile fileMemory = new RandomAccessFile(testMemoryFileName, "rw");
		
		fileMemory.write("# Some header\n".getBytes());
		fileMemory.write("info1     nothing    \n".getBytes());
		fileMemory.write(("MemFree:    " + (testTotalMemory - testUsedMemory) + " kB \n").getBytes());
		fileMemory.write("info2     nothing    \n".getBytes());
		fileMemory.write(("MemTotal:    " + testTotalMemory + " kB \n").getBytes());
		fileMemory.write("info3    nothing    \n".getBytes());
		
		fileMemory.close();
	}
	
	/*
	 * CPU util
	 */
	
	private void writeValidCPUInfoFile() throws IOException {
		RandomAccessFile fileCPUInfo = new RandomAccessFile(testCPUInfoFileName, "rw");
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU1ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu1Frequency + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu1CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu2CacheSize + " KB \n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU2ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu2Frequency + "\n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu3Frequency + "\n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU3ModelName + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu3CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.close();
	}
	
	private void writeCPUInfoFileWithMissingModelName() throws IOException {
		RandomAccessFile fileCPUInfo = new RandomAccessFile(testCPUInfoFileName, "rw");
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU1ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu1Frequency + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu1CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		// the second cpu has no model name
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu2CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu2Frequency + "\n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu3Frequency + "\n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU3ModelName + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu3CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.close();
	}
	
	private void writeCPUInfoFileWithMissingCPUFrequency() throws IOException {
		RandomAccessFile fileCPUInfo = new RandomAccessFile(testCPUInfoFileName, "rw");
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU1ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu1Frequency + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu1CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu2CacheSize + " KB \n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU2ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu2Frequency + "\n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU3ModelName + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu3CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.close();
	}
	
	private void writeCPUInfoFileWithMissingCacheSize() throws IOException {
		RandomAccessFile fileCPUInfo = new RandomAccessFile(testCPUInfoFileName, "rw");
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU1ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu1Frequency + "\n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu2CacheSize + " KB \n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU2ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu2Frequency + "\n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu3Frequency + "\n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU3ModelName + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu3CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.close();
	}
	
	private void writeCPUInfoFileWithNotNumericCPUFrequency() throws IOException {
		RandomAccessFile fileCPUInfo = new RandomAccessFile(testCPUInfoFileName, "rw");
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU1ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu1Frequency + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu1CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu2CacheSize + " KB \n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU2ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + "11word0" + "\n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu3Frequency + "\n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU3ModelName + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu3CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.close();
	}
	
	private void writeCPUInfoFileWithNotNumericCacheSize() throws IOException {
		RandomAccessFile fileCPUInfo = new RandomAccessFile(testCPUInfoFileName, "rw");
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU1ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu1Frequency + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu1CacheSize + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write(("cache size     :   " + cpu2CacheSize + " KB \n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU2ModelName + "\n").getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu2Frequency + "\n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.write("# Some header\n".getBytes());
		fileCPUInfo.write("info1  :   nothing    \n".getBytes());
		fileCPUInfo.write("info2  :   nothing    \n".getBytes());
		fileCPUInfo.write("# Some comments\n".getBytes());
		fileCPUInfo.write(("cpu MHz     :   " + cpu3Frequency + "\n").getBytes());
		fileCPUInfo.write(("model name     :   " + testCPU3ModelName + "\n").getBytes());
		fileCPUInfo.write(("cache size     :   " + "1word20" + " KB \n").getBytes());
		fileCPUInfo.write("info3  :   nothing    \n".getBytes());
		fileCPUInfo.write(("\n").getBytes());
		
		fileCPUInfo.close();
	}
	
	private void writeValidCPUUsageFile() throws IOException {
		RandomAccessFile fileCPUUsage = new RandomAccessFile(testCPUUsageFileName, "rw");
		
		fileCPUUsage.write("unused line 1\n".getBytes());
		fileCPUUsage.write("unused line 2\n".getBytes());
		//Cpu(s):  2.3%us,  0.6%sy,  0.1%ni, 96.6%id,  0.4%wa,  0.0%hi,  0.0%si,  0.0%st
		fileCPUUsage.write(("Cpu(s): " + cpuUserUsage + "%us, " + cpuSystemUsage + 
						"%sy, " + "anything, " + cpuIdle + "%id, thing1, thing2" + "\n").getBytes());
		fileCPUUsage.write("other line\n".getBytes());
		
		fileCPUUsage.close();
	}

	private void writeCPUUsageFileWithMissingUserUsage() throws IOException {
		RandomAccessFile fileCPUUsage = new RandomAccessFile(testCPUUsageFileName, "rw");
		
		fileCPUUsage.write("unused line 1\n".getBytes());
		fileCPUUsage.write("unused line 2\n".getBytes());
		//Cpu(s):  2.3%us,  0.6%sy,  0.1%ni, 96.6%id,  0.4%wa,  0.0%hi,  0.0%si,  0.0%st
		fileCPUUsage.write(("Cpu(s): " + cpuSystemUsage + 
						"%sy, " + "anything, " + cpuIdle + "%id, thing1, thing2" + "\n").getBytes());
		fileCPUUsage.write("other line\n".getBytes());
		
		fileCPUUsage.close();
	}

	private void writeCPUUsageFileWithMissingSystemUsage() throws IOException {
		RandomAccessFile fileCPUUsage = new RandomAccessFile(testCPUUsageFileName, "rw");
		
		fileCPUUsage.write("unused line 1\n".getBytes());
		fileCPUUsage.write("unused line 2\n".getBytes());
		//Cpu(s):  2.3%us,  0.6%sy,  0.1%ni, 96.6%id,  0.4%wa,  0.0%hi,  0.0%si,  0.0%st
		fileCPUUsage.write(("Cpu(s): " + cpuUserUsage + "%us, " + "anything, " + cpuIdle + "%id, thing1, thing2" + "\n").getBytes());
		fileCPUUsage.write("other line\n".getBytes());
		
		fileCPUUsage.close();
	}
	
	private void writeCPUUsageFileWithMissingIdle() throws IOException {
		RandomAccessFile fileCPUUsage = new RandomAccessFile(testCPUUsageFileName, "rw");
		
		fileCPUUsage.write("unused line 1\n".getBytes());
		fileCPUUsage.write("unused line 2\n".getBytes());
		//Cpu(s):  2.3%us,  0.6%sy,  0.1%ni, 96.6%id,  0.4%wa,  0.0%hi,  0.0%si,  0.0%st
		fileCPUUsage.write(("Cpu(s): " + cpuUserUsage + "%us, " + cpuSystemUsage + 
						"%sy, " + "anything, thing1, thing2" + "\n").getBytes());
		fileCPUUsage.write("other line\n".getBytes());
		
		fileCPUUsage.close();
	}
	
	private void writeCPUUsageWithNotNumericUserUsage() throws IOException {
		RandomAccessFile fileCPUUsage = new RandomAccessFile(testCPUUsageFileName, "rw");
		
		fileCPUUsage.write("unused line 1\n".getBytes());
		fileCPUUsage.write("unused line 2\n".getBytes());
		//Cpu(s):  2.3%us,  0.6%sy,  0.1%ni, 96.6%id,  0.4%wa,  0.0%hi,  0.0%si,  0.0%st
		fileCPUUsage.write(("Cpu(s): " + "1word0" + "%us, " + cpuSystemUsage + 
						"%sy, " + "anything, " + cpuIdle + "%id, thing1, thing2" + "\n").getBytes());
		fileCPUUsage.write("other line\n".getBytes());
		
		fileCPUUsage.close();
	}
	
	
	private void writeCPUUsageWithNotNumericSystemUsage() throws IOException {
		RandomAccessFile fileCPUUsage = new RandomAccessFile(testCPUUsageFileName, "rw");
		
		fileCPUUsage.write("unused line 1\n".getBytes());
		fileCPUUsage.write("unused line 2\n".getBytes());
		//Cpu(s):  2.3%us,  0.6%sy,  0.1%ni, 96.6%id,  0.4%wa,  0.0%hi,  0.0%si,  0.0%st
		fileCPUUsage.write(("Cpu(s): " + cpuUserUsage + "%us, " + "1word0" + 
						"%sy, " + "anything, " + cpuIdle + "%id, thing1, thing2" + "\n").getBytes());
		fileCPUUsage.write("other line\n".getBytes());
		
		fileCPUUsage.close();
	}

	private void writeCPUUsageWithNotNumericIdle() throws IOException {
		RandomAccessFile fileCPUUsage = new RandomAccessFile(testCPUUsageFileName, "rw");
		
		fileCPUUsage.write("unused line 1\n".getBytes());
		fileCPUUsage.write("unused line 2\n".getBytes());
		//Cpu(s):  2.3%us,  0.6%sy,  0.1%ni, 96.6%id,  0.4%wa,  0.0%hi,  0.0%si,  0.0%st
		fileCPUUsage.write(("Cpu(s): " + cpuUserUsage + "%us, " + cpuSystemUsage + 
						"%sy, " + "anything, " + "1word0" + "%id, thing1, thing2" + "\n").getBytes());
		fileCPUUsage.write("other line\n".getBytes());
		
		fileCPUUsage.close();
	}
}
