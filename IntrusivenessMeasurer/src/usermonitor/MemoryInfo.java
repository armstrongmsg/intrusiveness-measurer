package usermonitor;

import static commons.Preconditions.checkNonNegative;

public class MemoryInfo {
	private double totalMemory;
	private double usedMemory;
	
	public MemoryInfo(double totalMemory, double usedMemory) {
		checkNonNegative(totalMemory, "totalMemory must be non-negative");
		checkNonNegative(usedMemory, "usedMemory must be non-negative");

		this.totalMemory = totalMemory;
		this.usedMemory = usedMemory;
	}

	public double getTotalMemory() {
		return totalMemory;
	}

	public double getUsedMemory() {
		return usedMemory;
	}
}
