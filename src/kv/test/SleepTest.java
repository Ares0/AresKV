package kv.test;

import kv.KVConnection;
import kv.db.KVDataBase;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestRingQueue;
import kv.queue.ResponseArrayQueue;
import kv.synchro.SleepSynchronous;
import kv.synchro.SpinSynchronous;

public class SleepTest {

	public static void main(String[] args) {
		
//		testLinkedWrite();
		
//		testLinkedRead();
		
		testRingRead();
	}

	// 67ms
	public static void testLinkedWrite() {
		KVDataBase db = KVDataBase.getDatabase(16,
				new RequestLinkedQueue(), new ResponseArrayQueue(), new SleepSynchronous());
		KVConnection con = db.getConnection();
		
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 100000; i++) {
			con.put("" + i, "" + i);
		}
		con.close();
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
	// 100223ms
	public static void testLinkedRead() {
		KVDataBase db = KVDataBase.getDatabase(16, 
				new RequestLinkedQueue(), new ResponseArrayQueue(), new SpinSynchronous());
		KVConnection con = db.getConnection(new SleepSynchronous());
		
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 100000; i++) {
			con.put("" + i, "" + i);
		}
		
		for (int i = 0; i < 100000; i++) {
			con.get("" + i);
		}
		con.close();
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
	public static void testRingRead() {
		KVDataBase db = KVDataBase.getDatabase(16, new RequestRingQueue(128,
				new SleepSynchronous(), new SleepSynchronous()), new ResponseArrayQueue(), new SpinSynchronous());
		KVConnection con = db.getConnection(new SleepSynchronous());
		
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 100000; i++) {
			con.put("" + i, "" + i);
			con.get("" + i);
		}
		
		con.close();
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
}
