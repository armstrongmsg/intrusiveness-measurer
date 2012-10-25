package usermonitor;

import static commons.Preconditions.checkNonNegative;
import static commons.Preconditions.checkNotNull;

import java.util.List;

public class CPUInfo {
	private final List<CPUConfiguration> cpus;
	private final double systemUsage;
	private final double userUsage;
	private final double idle;
	
	public CPUInfo(List<CPUConfiguration> cpus, double systemUsage, double userUsage,
			double idle) {
		checkNotNull(cpus, "cpus must not be null.");
		checkNonNegative(systemUsage, "systemUsage must not be negative.");
		checkNonNegative(userUsage, "userUsage must not be negative.");
		checkNonNegative(idle, "idleUsage must not be negative.");
	
		this.cpus = cpus;
		this.systemUsage = systemUsage;
		this.userUsage = userUsage;
		this.idle = idle;
	}

	public List<CPUConfiguration> getCpus() {
		return cpus;
	}

	public double getSystemUsage() {
		return systemUsage;
	}

	public double getUserUsage() {
		return userUsage;
	}

	public double getIdle() {
		return idle;
	}
}
