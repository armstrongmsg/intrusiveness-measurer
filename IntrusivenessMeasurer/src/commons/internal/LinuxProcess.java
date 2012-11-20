package commons.internal;

import static java.lang.Runtime.getRuntime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commons.SystemProcess;

public class LinuxProcess implements SystemProcess {

	private static Logger logger = LoggerFactory.getLogger(LinuxProcess.class);  
	private static final int PS_XAU_PROCESSES_NAMES_COLUMN = 11;
	
	private Process process;
	private String name;
	
	public LinuxProcess(String name) throws IOException {
		this.name = name;
		this.process = null;
	}

	@Override
	public boolean isRunning() throws IOException {
		Process p = Runtime.getRuntime().exec("ps xau");
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = reader.readLine();
		boolean isRunning = false;
		boolean search = line != null;
		logger.debug("empty stream={}", !search);
		while (search) {
			String[] lineTokens = line.split("\\s+");
			checkPsXauOutput(lineTokens);
			if (lineTokens[PS_XAU_PROCESSES_NAMES_COLUMN - 1].contains(name)) {
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
	
	@Override
	public void terminate() throws IOException {
		// FIXME this is not good
		// this class enables to create two 
		// SystemProcess instances to the same 
		// process. But the second one does not 
		// have the Process instance to terminate
		// So i cannot stop the process through the
		// second instance.
		if (isRunning() && process != null) {
			process.destroy();			
		}
	}

	@Override
	public void execute() throws IOException {
		this.process = getRuntime().exec(name);
	}

	@Override
	public String getName() {
		return name;
	}
}
