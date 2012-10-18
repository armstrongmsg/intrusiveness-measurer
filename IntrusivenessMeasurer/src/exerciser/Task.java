package exerciser;

public interface Task {
	void run();
	TaskType type();
	double contention();
}
