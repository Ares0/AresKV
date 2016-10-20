package test.current;

import java.util.ArrayList;
import java.util.List;

import kv.db.DBFactory;
import kv.db.MasterSlaveDB;
import kv.db.MasterSlaveDB.DBState;
import kv.queue.RequestLinkedQueue;
import kv.queue.ResponseLinkedQueue;
import kv.synchro.SynchronousFactory;

public class KVConfig {

	public static void main(String[] args) {
		List<String> sa = new ArrayList<>();
		sa.add("127.0.0.1");
		
		MasterSlaveDB db = DBFactory.getMasterSlaveDB(new RequestLinkedQueue(), new ResponseLinkedQueue(), 
				SynchronousFactory.getSpinSynchronous(), new RequestLinkedQueue(), sa, DBState.LEADERING);
		try {
			db.start();
		} catch (InterruptedException e) {
			db.stop();
			e.printStackTrace();
		}
	}
	
}
