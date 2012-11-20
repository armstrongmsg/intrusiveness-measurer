package commons.internal;

import static commons.Preconditions.checkNotNull;
import static java.lang.Runtime.getRuntime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.OperatingSystem;

public class LinuxFacade implements OperatingSystem {

	private static Logger logger = LoggerFactory.getLogger(LinuxFacade.class);  
	private static final int PS_XAU_PROCESSES_NAMES_COLUMN = 11;
	
	@Override
	public Process execute(String command) throws IOException {
		checkNotNull(command, "command must not be null.");
		return getRuntime().exec(command);
	}

	@Override
	public boolean isRunning(String processName) throws IOException {
		Process p = Runtime.getRuntime().exec("ps xau");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = reader.readLine();
		boolean isRunning = false;
		boolean search = line != null;
		logger.debug("empty stream={}", !search);
		while (search) {
			String[] lineTokens = line.split("\\s+");
			checkPsXauOutput(lineTokens);
			if (lineTokens[PS_XAU_PROCESSES_NAMES_COLUMN - 1].contains(processName)) {
				isRunning = true;
				search = false;
			} else {
				line = reader.readLine();
				search = line != null;
			}
		}
		reader.close();
		return isRunning;
	}
	
	private static void checkPsXauOutput(String[] lineTokens) throws IOException {
		if (lineTokens.length < PS_XAU_PROCESSES_NAMES_COLUMN) {
			throw new IOException("Invalid input format. The stream may be corrupted or ps xau " +
					"command has generated an expected output.");
		}
	}
}
