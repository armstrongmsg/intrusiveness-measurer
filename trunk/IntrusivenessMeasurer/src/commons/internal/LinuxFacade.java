package commons.internal;

import static commons.Preconditions.checkNotNull;

import java.io.IOException;

import commons.OperatingSystem;
import commons.SystemProcess;

public class LinuxFacade implements OperatingSystem {
	
	@Override
	public SystemProcess execute(String command) throws IOException {
		checkNotNull(command, "command must not be null.");
		return new LinuxProcess(command);
	}

	@Override
	public boolean isRunning(String processName) throws IOException {
		checkNotNull(processName, "processName must not be null.");
		return new LinuxProcess(processName).isRunning();
	}
}
