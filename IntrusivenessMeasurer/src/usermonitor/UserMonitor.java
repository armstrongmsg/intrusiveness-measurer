package usermonitor;

import java.io.IOException;
import java.io.Serializable;

/**
 * This class is responsible by get some information from the user 
 * activity. Most of the informations concerns usage of resources 
 * like memory and CPU.
 * 
 * @author Armstrong Mardilson da Silva Goes, armstrongmsg@lsd.ufcg.edu.br
 */
public interface UserMonitor extends Serializable {
	
	/**
	 * Creates a new MemoryInfo instance containing data from 
	 * the user
	 * 
	 * @return the new MemoryInfo
	 * @throws IOException if there is some error when getting the 
	 * information from the system. These errors are possible because 
	 * the method may read files to obtain the information. The read 
	 * files may be corrupted, or the reading may be interrupted by 
	 * a low level error.
	 */
	MemoryInfo getMemoryInfo() throws IOException;
	
	/**
	 * Creates a new CPUInfo instance containing data from 
	 * the user.
	 * 
	 * @return the new CPUInfo
	 * @throws IOException if there is some error when getting the 
	 * information from the system. These errors are possible because 
	 * the method may read files to obtain the information. The read 
	 * files may be corrupted, or the reading may be interrupted by 
	 * a low level error.
	 */
	CPUInfo getCPUInfo() throws IOException;
}
