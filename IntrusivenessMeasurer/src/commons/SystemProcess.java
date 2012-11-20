package commons;

import java.io.IOException;

public interface SystemProcess {
	void execute() throws IOException;
	boolean isRunning() throws IOException;
	void terminate() throws IOException;
	String getName();
}
