package commons;

import java.io.IOException;

public interface OperatingSystem {
	SystemProcess execute(String command) throws IOException;
	boolean isRunning(String processName) throws IOException;
}
