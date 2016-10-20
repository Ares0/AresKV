package kv;

import java.util.HashMap;
import java.util.Map;

import kv.db.ClusterDB;
import kv.db.DBFactory;
import kv.queue.RequestLinkedQueue;
import kv.queue.ResponseLinkedQueue;
import kv.synchro.SpinSynchronous;
import kv.utils.Range;

public class KVConfig {

	public static void main(String[] args) {
		Map<String, Range> clusterRange = new HashMap<String, Range>();
		clusterRange.put("1", new Range(1025, 2048));
		clusterRange.put("2", new Range(2049, 4096));
		
		ClusterDB db = DBFactory.getClusterDB(0, 1024, new RequestLinkedQueue(), new ResponseLinkedQueue(), new SpinSynchronous(), clusterRange);
		try {
			db.start();
		} catch (InterruptedException e) {
			db.stop();
			e.printStackTrace();
		}
	}
	
}
