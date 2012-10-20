package usermonitor.internal;

import static commons.Preconditions.check;
import static commons.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
		
		logger.info("Started using {} as memory info file.", memoryInfoFilename);
		logger.info("Started using {} as cpu info file.", cpuInfoFilename);
		
		checkNotNull(memoryInfoFilename, "memoryInfoFileName must not be null.");
		checkNotNull(cpuInfoFilename, "cpuInfoFileName must not be null.");
		
		checkFileExist(cpuInfoFilename);
		check(new File(cpuInfoFilename).canRead(), "Can't read cpuInfoFileName.");		
		
		checkFileExist(memoryInfoFilename);
		check(new File(memoryInfoFilename).canRead(), "Can't read memoryInfoFileName.");		
		
		memoryInfoFile = new RandomAccessFile(memoryInfoFilename, "r");		
		CPUInfoFile = new RandomAccessFile(cpuInfoFilename, "r");
	}
	
	private void checkFileExist(String fileName) throws FileNotFoundException {
		if (!new File(fileName).exists()) {
			throw new FileNotFoundException(fileName + " was not found.");
		}
	}
	
	@Override
	public MemoryInfo getMemoryUsage() throws IOException {
		
		double totalMemory = -1;
		double freeMemory = -1;
		
		while (totalMemory == -1 || freeMemory == -1) {
			String line = memoryInfoFile.readLine();
			if (line == null) {
				throw new IOException("Could not find all necessary information in memory info file.");
			}
			
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

	@Override
	public CPUInfo getCPUUsage() {
		return null;
	}
}
