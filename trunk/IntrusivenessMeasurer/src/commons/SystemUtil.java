package commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SystemUtil.class);  
	private static final int PS_XAU_PROCESSES_NAMES_COLUMN = 11;

	public static boolean processIsRunning(String name) throws IOException {
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
}
