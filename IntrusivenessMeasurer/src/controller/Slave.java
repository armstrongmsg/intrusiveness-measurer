package controller;

import java.io.Serializable;
import java.util.List;

import exerciser.Task;

public interface Slave extends Serializable {
	void stop();
	// I think this should be used by the user interface
	void stopRunningTasks();
	List<Task> getRunningTasks();
}
