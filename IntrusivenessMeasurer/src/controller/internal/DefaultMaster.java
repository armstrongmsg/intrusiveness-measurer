package controller.internal;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import usermonitor.UserMonitor;
import br.edu.ufcg.lsd.beefs.commons.config.BasicConfiguration;
import br.edu.ufcg.lsd.beefs.commons.config.Configuration;
import br.edu.ufcg.lsd.beefs.commons.persistence.jdbm.JDBMMapFactory;

import commons.MapFactory;

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
	private Map<Slave, UserMonitor> userMonitors;
	//private PersistentMap<Task, Slave> runningTasks;
	private TaskScheduler scheduler;
	private ResultsGetter getter;
	private ResultsGetter getter0;
	
	
	public DefaultMaster(MapFactory factory) throws IOException {
		userMonitors = factory.createMap("monitors");
	}
	
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
	
	public static void main(String[] args) throws IOException {
		
		JDBMMapFactory factory = new JDBMMapFactory();
		BasicConfiguration<Configuration> conf = new BasicConfiguration<Configuration>();
		Properties p = new Properties();
		p.put("metadata_directory", "metadata");
		p.put("metadata_filename", "file");
		
		conf.configure(p);
		
		factory.initialize(conf);
		
		Map<String, Integer> map = factory.createMap("map");
		map.put("aaa", 10);
		map.put("aaa", 20);
	}
}
