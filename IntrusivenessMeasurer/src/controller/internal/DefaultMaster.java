package controller.internal;

import java.io.IOException;
import java.util.List;

import commons.PersistentMap;

import usermonitor.CPUInfo;
import usermonitor.MemoryInfo;
import usermonitor.UserMonitor;
import controller.Master;
import controller.RegisterService;
import controller.Slave;
import exerciser.ResultsGetter;
import exerciser.RunningResults;
import exerciser.Task;
import exerciser.TaskScheduler;

// this code was implemented with the goal of help 
// the design of the tool.
public class DefaultMaster implements Master {

	private RegisterService registerService;
	//private PersistentMap<Slave, UserMonitor> userMonitors;
	//private PersistentMap<Task, Slave> runningTasks;
	private TaskScheduler scheduler;
	private ResultsGetter getter;
	
	@Override
	public void completed(Task task) {
		RunningResults results = null;
		//if (runningTasks.remove(task) != null) {
	//		results = getter.getResults(task);
	//	}
	}
	
	public void run() throws IOException {
		boolean run = true;
		while (run) {
			List<Slave> onlineSlaves = registerService.getOnlineSlaves();
			
			for (Slave slave : onlineSlaves) {
			//	UserMonitor monitor = userMonitors.get(slave);
			//	CPUInfo cpuInfo = monitor.getCPUInfo();
			//	MemoryInfo memoryInfo = monitor.getMemoryInfo();
				
				/*
				if (hasTaskToBeScheduled(slave)) {
					Task taskToScheduled = getTaskToBeScheduled(cpuInfo, memoryInfo);
					scheduler.schedule(taskToScheduled);
				}*/
			}
		}
	}
}
