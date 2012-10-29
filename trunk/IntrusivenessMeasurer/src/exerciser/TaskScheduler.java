package exerciser;

public interface TaskScheduler {
	void schedule(Task task);
	void stop(Task task);
	void stopAll();
}
