package usermonitor.internal;

import static commons.Preconditions.checkNotNull;
import static commons.util.FileUtil.checkFileExist;
import static commons.util.FileUtil.checkFileIsReadable;
import static commons.util.FileUtil.getNextLineOfData;
import static commons.util.FileUtil.jumpLines;
import static commons.util.FileUtil.readUntilFindBlankLine;
import static commons.util.StringUtil.isNumeric;

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
 * This implementation of UserMonitor reads the data from the files passed in the 
 * constructor to create the user info objects.
 * 
 * @author Armstrong Mardilson da Silva Goes, armstrongmsg@lsd.ufcg.edu.br
 */
public class DefaultUserMonitor implements UserMonitor {

	private static final Logger logger = LoggerFactory.getLogger(DefaultUserMonitor.class);
	private final RandomAccessFile memoryInfoFile;
	private final RandomAccessFile cpuConfigurationFile;
	private final RandomAccessFile cpuUsageFile;
	
	/**
	 * The line that contains the total memory information must start with this header.
	 */
	private static final String TOTAL_MEMORY_LINE_HEADER = "MemTotal:";
	/**
	 * The line that contains the free memory information must start with this header.
	 */
	private static final String FREE_MEMORY_LINE_HEADER = "MemFree:";
	/**
	 * The line that contains the cpu model name must start with this header.
	 */
	private static final String CPU_MODEL_NAME_LINE_HEADER = "model name";
	/**
	 * The line that contains the cpu frequency must start with this header.
	 */
	private static final String CPU_FREQUENCY_LINE_HEADER = "cpu MHz";
	/**
	 * The line that contains the cpu cache size must start with this header.
	 */
	private static final String CPU_CACHE_SIZE_LINE_HEADER = "cache size";

	private static final String CACHE_SIZE_UNIT_STRING = "KB";
	
	/**
	 * Constructs a new DefaultUserMonitor instance which will read from the 
	 * passed files.</br>
	 * 
	 * The file whose name is memoryInfoFileName is used to get the total memory amount and the free memory 
	 * amount. The expected file pattern is the following:</br>
	 * </br>
	 * [ignored lines]</br>
	 * TOTAL_MEMORY_LINE_HEADER : total memory kB</br>
	 * [ignored lines]</br>
	 * FREE_MEMORY_LINE_HEADER : free memory kB</br>
	 * [ignored lines]</br>
	 * </br>
	 * The free memory may be in upper lines than the total memory.</br>
	 * </br>
	 * 
	 * The file whose name is cpuConfigurationFileName is used to get the CPU configurations, like CPU frequency, 
	 * cache size and model name. The expected file pattern is the following:</br>  
	 * </br>
	 * (cpu 0)</br>
	 * [ignored lines]</br>
	 * CPU_MODEL_NAME_LINE_HEADER : model name</br>
	 * [ignored lines]</br>
	 * CPU_FREQUENCY_LINE_HEADER : CPU frequency</br> 
	 * [ignored lines]</br>
	 * CPU_CACHE_SIZE_LINE_HEADER : cache size CACHE_SIZE_UNIT_STRING</br>
	 * [ignored lines]</br>
	 * </br>
	 * (cpu 1)</br>
	 * [ignored lines]</br>
	 * CPU_MODEL_NAME_LINE_HEADER : model name</br>
	 * [ignored lines]</br>
	 * CPU_FREQUENCY_LINE_HEADER : CPU frequency</br> 
	 * [ignored lines]</br>
	 * CPU_CACHE_SIZE_LINE_HEADER : cache size CACHE_SIZE_UNIT_STRING</br>
	 * [ignored lines]</br>
	 * </br>
	 * ...</br>
	 * (cpu n)</br>
	 * ...</br>
	 * </br>
	 * The read lines may be in different order.</br>
	 * </br>
	 * 
	 * The file whose name is cpuUsageFileName is used to get the CPU system usage, user usage and idle CPU.
	 * This file's pattern is the pattern of a typical call to top program. The expected pattern is the following:</br>
	 * </br>
	 * [ignored line]</br>
	 * [ignored line]</br>
	 * Cpu(s):  {CPU user usage}%us,  {CPU system usage}%sy,  ignored, {CPU idle}%id,  ignored, ignored, ...</br>
	 * [ignored lines]</br>
	 * 
	 * @param memoryInfoFilename The name of the file that contains the informations about memory.
	 * @param cpuConfigurationFilename The name of the file that contains the informations about CPU configuration.
	 * @param cpuUsageFilename The name of the file that contains the informations about CPU usage.
	 * @throws IOException 
	 * @throws IllegalArgumentException if one or more of the arguments is null or any of the 
	 * passed files is not-readable.
	 */
	public DefaultUserMonitor(String memoryInfoFilename, 
							String cpuConfigurationFilename, String cpuUsageFilename) throws IOException {
		checkNotNull(memoryInfoFilename, "memoryInfoFileName must not be null.");
		checkNotNull(cpuConfigurationFilename, "cpuInfoFileName must not be null.");
		checkNotNull(cpuUsageFilename, "cpuUsageFileName must not be null.");
		
		logger.info("Started using {} as memory info file.", memoryInfoFilename);
		logger.info("Started using {} as cpu configuration file.", cpuConfigurationFilename);
		logger.info("Started using {} as cpu usage file.", cpuUsageFilename);
		
		checkFileExist(cpuConfigurationFilename);
		checkFileIsReadable(cpuConfigurationFilename);
		
		checkFileExist(cpuUsageFilename);
		checkFileIsReadable(cpuUsageFilename);
		
		checkFileExist(memoryInfoFilename);
		checkFileIsReadable(memoryInfoFilename);
		
		memoryInfoFile = new RandomAccessFile(memoryInfoFilename, "r");	
		cpuConfigurationFile = new RandomAccessFile(cpuConfigurationFilename, "r");
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
		cpuConfigurationFile.seek(0);
	}
	
	private CPUConfiguration readCPUFromFile() throws IOException {
		
		double cpuFrequency = -1;
		String modelName = null;
		double cacheSize = -1;

		// while there are fields to read ...
		while (cpuFrequency == -1 || cacheSize == -1 || modelName == null) {
			String line = getNextLineOfData(cpuConfigurationFile);
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
		readUntilFindBlankLine(cpuConfigurationFile);
		return new CPUConfiguration(cpuFrequency, modelName, cacheSize);
	}

	private boolean thereAreCPUsToRead() throws IOException {
		String line = cpuConfigurationFile.readLine();
		boolean thereAre = line != null && !line.equals("");
		// reset the file to the position it was before doing the checking
		cpuConfigurationFile.seek(cpuConfigurationFile.getFilePointer() - (line == null ? 0 : line.length()));
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
