package kv.synchro;

import kv.db.KVDataBase;
import kv.queue.RequestQueue;

public class ReqConditionSynchronous implements Synchronous {

	@Override
	public void doSynchronous() {
		KVDataBase db = KVDataBase.getDatabase();
		RequestQueue req = db.getRequestQueue();
		synchronized (req) {
			try {
				req.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
