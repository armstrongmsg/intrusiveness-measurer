package usermonitor;

import java.io.IOException;

public interface UserMonitor {
	MemoryInfo getMemoryInfo() throws IOException;
	CPUInfo getCPUInfo() throws IOException;
}
