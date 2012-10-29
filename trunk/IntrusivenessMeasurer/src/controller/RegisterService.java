package controller;

import java.util.List;

import commons.FailureListener;

public interface RegisterService extends FailureListener {
	void register(Slave slave);
	List<Slave> getKnownSlaves(); 
	List<Slave> getOnlineSlaves(); 
}
