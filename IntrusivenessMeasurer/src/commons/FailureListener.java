package commons;

import controller.Slave;

public interface FailureListener {
	void onFailure(Slave slave);
}
