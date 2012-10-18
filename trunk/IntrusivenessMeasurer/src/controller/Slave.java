package controller;

import java.util.List;

import exerciser.Task;

public interface Slave {
	void stop();
	// I think this should be used by the user interface
	void stopRunningTasks();
	List<Task> getRunningTasks();
}
