package usermonitor;

import java.io.IOException;

public interface UserMonitor {
	MemoryInfo getMemoryUsage() throws IOException;
	CPUInfo getCPUUsage();
}
