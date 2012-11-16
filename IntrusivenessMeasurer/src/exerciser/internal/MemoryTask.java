package exerciser.internal;

import static commons.FileUtil.checkFileExist;
import static commons.FileUtil.checkFileIsExecutable;
import static commons.Preconditions.checkNonNegative;
import static commons.Preconditions.checkNotNull;
import static commons.StringUtil.concat;
import static commons.SystemUtil.processIsRunning;
import static exerciser.TaskType.MEMORY;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.valueOf;

import java.io.IOException;

import exerciser.Task;
import exerciser.TaskType;

/**
 * This is an implementation of {@link Task} which exercises 
 * memory resources. It does it by allocating the given amount 
 * of bytes and freeing the memory passed the given time. 
 * It calls a binary to do the allocation and deallocation of the memory.
 * 
 * @author Armstrong Mardilson da Silva Goes
 */
public class MemoryTask implements Task {

	/**
	 * The path to the binary which allocate and deallocate memory.
	 */
	private String memoryExerciser; 
	
	/**
	 * The process which represents this {@link MemoryTask}. It can be null when there is no 
	 * process running.
	 */
	private Process runningProcess;
	
	private int amountOfBytesToAllocate;
	private int timeout;
	
	/**
	 * @param memoryExerciser The path to the binary which allocate and deallocate memory. It must be 
	 * non-null and a executable file.
	 * @param amountOfBytesToAllocate It must be non-negative.
	 * @param timeout The time that the exerciser will wait until deallocate the memory. It must be 
	 * non-negative.
	 * @throws IOException If memoryExerciser is not executable.
	 * @throws IllegalArgumentException If any of the arguments is null, or any of the numeric 
	 * arguments is negative.
	 */
	public MemoryTask(String memoryExerciser, int amountOfBytesToAllocate,
			int timeout) throws IOException {
		checkNotNull(memoryExerciser, "memoryExerciser must be non-null.");
		checkNonNegative(amountOfBytesToAllocate, "amountOfBytesToAllocate must be non-negative.");
		checkNonNegative(timeout, "timeout must be non-negative.");
		
		checkFileExist(memoryExerciser);
		checkFileIsExecutable(memoryExerciser);
		
		this.memoryExerciser = memoryExerciser;
		this.amountOfBytesToAllocate = amountOfBytesToAllocate;
		this.timeout = timeout;
	}

	@Override
	public void run() throws IOException {
		String command = concat(memoryExerciser, " ",
						 valueOf(amountOfBytesToAllocate), " ", 
						 valueOf(timeout));
		runningProcess = getRuntime().exec(command);
	}

	@Override
	public TaskType type() {
		return MEMORY;
	}

	@Override
	public void terminate() throws IOException {
		if (isRunning()) {
			runningProcess.destroy();			
			runningProcess = null;
		}
	}

	@Override
	public boolean isRunning() throws IOException {
		return processIsRunning(memoryExerciser);
	}
}
