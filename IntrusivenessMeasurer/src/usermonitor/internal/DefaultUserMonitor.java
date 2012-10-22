package usermonitor.internal;

import static commons.Preconditions.check;
import static commons.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import usermonitor.CPU;
import usermonitor.CPUInfo;
import usermonitor.MemoryInfo;
import usermonitor.UserMonitor;

public class DefaultUserMonitor implements UserMonitor {

	private static final Logger logger = LoggerFactory.getLogger(DefaultUserMonitor.class);
	private final RandomAccessFile memoryInfoFile;
	private final RandomAccessFile cpuInfoFile;
	private final RandomAccessFile cpuUsageFile;
	
	private static final String TOTAL_MEMORY_LINE_HEADER = "MemTotal:";
	private static final String FREE_MEMORY_LINE_HEADER = "MemFree:";
	private static final String CPU_MODEL_NAME_LINE_HEADER = "model name";
	private static final String CPU_FREQUENCY_LINE_HEADER = "cpu MHz";
	private static final String CPU_CACHE_SIZE_LINE_HEADER = "cache size";
	
	public DefaultUserMonitor(String memoryInfoFilename, 
							String cpuInfoFilename, String cpuUsageFilename) throws FileNotFoundException {
		checkNotNull(memoryInfoFilename, "memoryInfoFileName must not be null.");
		checkNotNull(cpuInfoFilename, "cpuInfoFileName must not be null.");
		checkNotNull(cpuUsageFilename, "cpuUsageFileName must not be null.");
		
		logger.info("Started using {} as memory info file.", memoryInfoFilename);
		logger.info("Started using {} as cpu info file.", cpuInfoFilename);
		logger.info("Started using {} as cpu usage file.", cpuUsageFilename);
		
		checkFileExist(cpuInfoFilename);
		check(new File(cpuInfoFilename).canRead(), "Can't read cpuInfoFileName.");		
		
		checkFileExist(cpuUsageFilename);
		check(new File(cpuUsageFilename).canRead(), "Can't read cpuUsageFileName.");		
		
		checkFileExist(memoryInfoFilename);
		check(new File(memoryInfoFilename).canRead(), "Can't read memoryInfoFileName.");		
		
		memoryInfoFile = new RandomAccessFile(memoryInfoFilename, "r");		
		cpuInfoFile = new RandomAccessFile(cpuInfoFilename, "r");
		cpuUsageFile = new RandomAccessFile(cpuUsageFilename, "r");
	}
	
	private void checkFileExist(String fileName) throws FileNotFoundException {
		if (!new File(fileName).exists()) {
			throw new FileNotFoundException(fileName + " was not found.");
		}
	}
	
	@Override
	public MemoryInfo getMemoryInfo() throws IOException {
		
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
	public CPUInfo getCPUInfo() throws IOException {
		jumpLines(cpuUsageFile, 2);
		String cpuInfoLine = cpuUsageFile.readLine();
		String[] tokens = cpuInfoLine.split("\\s+");
		
		double userUsage = new Double(tokens[1].split("%")[0]);
		double systemUsage = new Double(tokens[2].split("%")[0]);
		double idle = new Double(tokens[4].split("%")[0]);
		
		return new CPUInfo(readCPUsFromCPUInfoFile(), systemUsage, userUsage, idle);
	}

	private void jumpLines(RandomAccessFile file, int numberOfLines) throws IOException {
		for (int i = 0; i < numberOfLines; i++) {
			if (file.readLine() == null) {
				throw new IOException("Could not jump lines from the file.");
			}
		}
	}
	
	private List<CPU> readCPUsFromCPUInfoFile() throws IOException {
		ArrayList<CPU> cpus = new ArrayList<CPU>();
		CPU next = null;
		
		do {
			logger.debug("cpu: {}", cpus.size());
			next = readCPUFromFile();
			if (next != null) {
				cpus.add(next);
			}
		}
		while (thereAreCPUsToRead());
		
		return cpus;
	}
	
	private boolean thereAreCPUsToRead() throws IOException {
		String line = cpuInfoFile.readLine();
		boolean thereAre = line != null && !line.equals(""); 
		cpuInfoFile.seek(cpuInfoFile.getFilePointer() - (line == null ? 0 : line.length()));
		return thereAre;
	}
	
	private void readUntilFindBlankLine(RandomAccessFile file) throws IOException {
		String line = file.readLine();
		while (line != null && !line.equals("")) {
			line = file.readLine();
		}
	}
	
	private CPU readCPUFromFile() throws IOException {
		
		double cpuFrequency = -1;
		String modelName = null;
		double cacheSize = -1;

		// while there are fields to read ...
		while (cpuFrequency == -1 || cacheSize == -1 || modelName == null) {
			String line = cpuInfoFile.readLine();
			logger.debug("read line: " + line);
			
			// if there is no more info in the middle of a cpu reading ...
			if (line == null || line.equals("")) {
				throw new IOException("Could not find all necessary information in memory info file.");
			}
			
			String[] tokens = line.split(":");
			
			if (tokens[0].trim().equals(CPU_MODEL_NAME_LINE_HEADER)) {
				modelName = tokens[1].trim();
			} else if (tokens[0].trim().equals(CPU_FREQUENCY_LINE_HEADER)) {
				cpuFrequency = new Double(tokens[1].trim());		
			} else if (tokens[0].trim().equals(CPU_CACHE_SIZE_LINE_HEADER)) {
				cacheSize = new Double(tokens[1].trim());
			}
		}
		
		// read the unnecessary data, to prepare to read the next cpu
		readUntilFindBlankLine(cpuInfoFile);
		return new CPU(cpuFrequency, modelName, cacheSize);
	}
}
