package usermonitor;

import static commons.Preconditions.checkNonNegative;
import static commons.Preconditions.checkNotNull;

public class CPUConfiguration {
	private final double cpuFrequency;
	private final String modelName;
	private final double cacheSize;
	
	public CPUConfiguration(double cpuFrequency, String modelName, double cacheSize) {
		checkNonNegative(cpuFrequency, "cpuFrequency must not be negative.");
		checkNotNull(modelName, "modelName must not be null.");
		checkNonNegative(cacheSize, "cacheSize must not be negative.");
		
		this.cpuFrequency = cpuFrequency;
		this.modelName = modelName;
		this.cacheSize = cacheSize;
	}

	public double getCpuFrequency() {
		return cpuFrequency;
	}

	public String getModelName() {
		return modelName;
	}

	public double getCacheSize() {
		return cacheSize;
	}
}

