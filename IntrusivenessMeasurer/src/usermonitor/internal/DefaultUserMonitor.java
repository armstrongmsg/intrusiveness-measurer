package usermonitor.internal;

import static commons.Preconditions.check;
import static commons.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import usermonitor.CPUInfo;
import usermonitor.MemoryInfo;
import usermonitor.UserMonitor;

public class DefaultUserMonitor implements UserMonitor {

	private static final Logger logger = LoggerFactory.getLogger(DefaultUserMonitor.class);
	private final RandomAccessFile memoryInfoFile;
	private final RandomAccessFile CPUInfoFile;
	
	private static final String TOTAL_MEMORY_LINE_HEADER = "MemTotal:";
	private static final String FREE_MEMORY_LINE_HEADER = "MemFree:";
	
	public DefaultUserMonitor(String memoryInfoFilename, 
							String cpuInfoFilename) throws FileNotFoundException {
		
		logger.info("Using {} as memory info file.", memoryInfoFilename);
		logger.info("Using {} as cpu info file.", cpuInfoFilename);
		
		checkNotNull(memoryInfoFilename, "memoryInfoFileName must not be null.");
		checkNotNull(cpuInfoFilename, "cpuInfoFileName must not be null.");
		
		check(new File(cpuInfoFilename).exists(), "cpuInfoFileName does not exist.");
		check(new File(cpuInfoFilename).canRead(), "Can't read cpuInfoFileName.");		
		
		check(new File(memoryInfoFilename).exists(), "memoryInfoFileName does not exist.");
		check(new File(memoryInfoFilename).canRead(), "Can't read memoryInfoFileName.");		
		
		memoryInfoFile = new RandomAccessFile(memoryInfoFilename, "r");		
		CPUInfoFile = new RandomAccessFile(cpuInfoFilename, "r");
	}
	
	@Override
	public MemoryInfo getMemoryUsage() throws IOException {
		
		double totalMemory = -1;
		double freeMemory = -1;
		
		while (totalMemory == -1 || freeMemory == -1) {
			if (reachedEndOfFile(memoryInfoFile)) {
				throw new IOException("Could not find all necessary information in memory info file.");
			}
			String line = memoryInfoFile.readLine();
			String[] tokens = line.split("\\s+");
			
			if (tokens[0].trim().equals(TOTAL_MEMORY_LINE_HEADER)) {
				totalMemory = new Double(tokens[1]);
			} else if (tokens[0].trim().equals(FREE_MEMORY_LINE_HEADER)) {
				freeMemory = new Double(tokens[1]);		
			}
		}
		
		memoryInfoFile.seek(0);
		
		return new MemoryInfo(totalMemory, totalMemory - freeMemory);
	}

	private boolean reachedEndOfFile(RandomAccessFile memoryInfoFile) throws IOException {
		return memoryInfoFile.getFilePointer() == memoryInfoFile.length();
	}

	@Override
	public CPUInfo getCPUUsage() {
		return null;
	}
	
	public static void main(String[] args) {
		String str = "MemTotal:        2060468 kB";
		
		System.out.println(Arrays.toString(str.split("\\s+")));
	}
}
