package usermonitor.internal;

import static commons.FileUtil.checkFileExist;
import static commons.FileUtil.getNextLineOfData;
import static commons.FileUtil.jumpLines;
import static commons.FileUtil.readUntilFindBlankLine;
import static commons.Preconditions.check;
import static commons.Preconditions.checkNotNull;
import static commons.StringUtil.isNumeric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import usermonitor.CPUConfiguration;
import usermonitor.CPUInfo;
import usermonitor.MemoryInfo;
import usermonitor.UserMonitor;

/**
 * TODO make doc
 * @author armstrong
 *
 */
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
	
	// FIXME improve this code
	// maybe create a class only with measures
	private static final String CACHE_SIZE_UNIT_STRING = "KB";
	
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
	
	@Override
	public MemoryInfo getMemoryInfo() throws IOException {
		double totalMemory = -1;
		double freeMemory = -1;
		
		while (totalMemory == -1 || freeMemory == -1) {
			String line = getNextLineOfData(memoryInfoFile);
			String[] tokens = line.split("\\s+");

			if (tokens[0].trim().equals(TOTAL_MEMORY_LINE_HEADER)) {
				// FIXME resolve this duplication
				if (!isNumeric(tokens[1])) {
					throw new IOException("Invalid format of memory info file.");
				}
				totalMemory = new Double(tokens[1]);
			} else if (tokens[0].trim().equals(FREE_MEMORY_LINE_HEADER)) {
				if (!isNumeric(tokens[1])) {
					throw new IOException("Invalid format of memory info file.");
				}
				freeMemory = new Double(tokens[1]);		
			}
		}
		
		rewindMemoryInfoFile();
		return new MemoryInfo(totalMemory, totalMemory - freeMemory);
	}

	private void rewindMemoryInfoFile() throws IOException {
		memoryInfoFile.seek(0);
	}
	
	@Override
	public CPUInfo getCPUInfo() throws IOException {
		String[] tokens = getTokensFromCPUUsageLine(readCPUUsageLine());

		if (!isNumeric(tokens[1].split("%")[0]) || !isNumeric(tokens[2].split("%")[0])
						|| !isNumeric(tokens[4].split("%")[0])) {
			throw new IOException("Invalid format of CPU usage file.");
		}

		List<CPUConfiguration> configurations = readCPUsFromCPUInfoFile();
		rewindCPUFiles();
		return new CPUInfo(configurations, new Double(tokens[2].split("%")[0]), 
							new Double(tokens[1].split("%")[0]), 
							new Double(tokens[4].split("%")[0]));
	}

	private String readCPUUsageLine() throws IOException {
		jumpLines(cpuUsageFile, 2);
		return cpuUsageFile.readLine();
	}
	
	private String[] getTokensFromCPUUsageLine(String line) throws IOException {
		String[] tokens = line.split("\\s+");
		if (tokens.length < 5) {
			throw new IOException("Invalid format of CPU usage file.");
		}
		return tokens;
	}

	private List<CPUConfiguration> readCPUsFromCPUInfoFile() throws IOException {
		ArrayList<CPUConfiguration> cpus = new ArrayList<CPUConfiguration>();
		do {
			cpus.add(readCPUFromFile());
		}
		while (thereAreCPUsToRead());
		return cpus;
	}
	
	private void rewindCPUFiles() throws IOException {
		cpuUsageFile.seek(0);
		cpuInfoFile.seek(0);
	}
	
	private CPUConfiguration readCPUFromFile() throws IOException {
		
		double cpuFrequency = -1;
		String modelName = null;
		double cacheSize = -1;

		// while there are fields to read ...
		while (cpuFrequency == -1 || cacheSize == -1 || modelName == null) {
			String line = getNextLineOfData(cpuInfoFile);
			String[] tokens = line.split(":");

			if (tokens[0].trim().equals(CPU_MODEL_NAME_LINE_HEADER)) {
				modelName = tokens[1].trim();
			} else if (tokens[0].trim().equals(CPU_FREQUENCY_LINE_HEADER)) {
				checkCPUFrequencyToken(tokens[1]);
				cpuFrequency = new Double(tokens[1].trim());		
			} else if (tokens[0].trim().equals(CPU_CACHE_SIZE_LINE_HEADER)) {
				checkCacheSizeToken(tokens[1]);
				cacheSize = new Double(tokens[1].split(CACHE_SIZE_UNIT_STRING)[0].trim());
			}
		}
		
		// read the unnecessary data, to prepare to read the next cpu
		readUntilFindBlankLine(cpuInfoFile);
		return new CPUConfiguration(cpuFrequency, modelName, cacheSize);
	}

	private boolean thereAreCPUsToRead() throws IOException {
		String line = cpuInfoFile.readLine();
		boolean thereAre = line != null && !line.equals("");
		// reset the file to the position it was before doing the checking
		cpuInfoFile.seek(cpuInfoFile.getFilePointer() - (line == null ? 0 : line.length()));
		return thereAre;
	}
	
	private void checkCPUFrequencyToken(String token) throws IOException {
		if (!isNumeric(token.trim())) {
			throw new IOException("Invalid format of CPU info file.");
		}
	}

	private void checkCacheSizeToken(String token) throws IOException {
		if (!isNumeric(token.split(CACHE_SIZE_UNIT_STRING)[0].trim())) {
			throw new IOException("Invalid format of CPU info file.");
		}
	}
}
