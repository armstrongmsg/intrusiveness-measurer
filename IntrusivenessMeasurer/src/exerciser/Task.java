package exerciser;

import java.io.IOException;

/**
 * Abstract representation of a process which can 
 * run in the machine where the code runs. A {@link Task} 
 * can use different types and amounts of resources and take 
 * different times to complete.
 * 
 * @author Armstrong Mardilson da Silva Goes
 */
public interface Task {
	
	/**
	 * Makes the {@link Task} processing to be started. 
	 * This can mean to create many underlying processes, 
	 * to create and use many files, allocate memory and
	 * other types of resource usage.
	 * 
	 * @throws IOException If there's an error while creating the 
	 * processes or setting the environment to the {@link Task}. As
	 * it may create and use files while starting up, this is reasonable.
	 */
	void run() throws IOException;
	
	/**
	 * Returns a {@link TaskType} object which represents the type of 
	 * this {@link Task}.
	 * 
	 * @return The object.
	 */
	TaskType type();
	
	/**
	 * Makes the {@link Task} processing to be finished. 
	 * If the {@link Task} is no longer running, it does nothing.
	 * 
	 * @throws IOException If the stopping generate errors
	 * in underlying streams.
	 */
	void terminate() throws IOException;
	
	/**
	 * Returns {@link Boolean#True} if the {@link Task} is still running. 
	 * Returns {@link Boolean#False} otherwise.

	 * @throws IOException If occurred an error while reading 
	 * {@link Task} informations from streams. 
	 */
	boolean isRunning() throws IOException;
}
