package kv.synchro;

import kv.db.KVDataBase;
import kv.queue.ResponseQueue;

public class RepConditionSynchronous implements Synchronous {

	@Override
	public void doSynchronous() {
		KVDataBase db = KVDataBase.getDatabase();
		ResponseQueue rep = db.getResponseQueue();
		synchronized (rep) {
			try {
				rep.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
