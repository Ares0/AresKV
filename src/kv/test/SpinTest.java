package kv.test;

import kv.KVConnection;
import kv.db.KVDataBase;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestRingQueue;
import kv.queue.ResponseArrayQueue;
import kv.synchro.SpinSynchronous;

public class SpinTest {

	public static void main(String[] args) {
		
//		testLinkedWrite();
		
//		testLinkedRead();
		
		testRingReadWrite();
	}
	
	// 72ms
	public static void testLinkedWrite() {
		KVDataBase db = KVDataBase.getDatabase(16, new RequestLinkedQueue(), new ResponseArrayQueue(), new SpinSynchronous());
		KVConnection con = db.getConnection(new SpinSynchronous());
		
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 100000; i++) {
			con.put("" + i, "" + i);
		}
		con.close();
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
	// 179642ms
	public static void testLinkedReadWrite() {
		KVDataBase db = KVDataBase.getDatabase(16, new RequestLinkedQueue(), new ResponseArrayQueue(), new SpinSynchronous());
		KVConnection con = db.getConnection(new SpinSynchronous());
		
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
	
	public static void testRingReadWrite() {
		KVDataBase db = KVDataBase.getDatabase(16, new RequestRingQueue(128,
				new SpinSynchronous(), new SpinSynchronous()), new ResponseArrayQueue(), new SpinSynchronous());
		KVConnection con = db.getConnection(new SpinSynchronous());
		
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 100000; i++) {
			con.put("" + i, "" + i);
			con.get("" + i);
		}
		
		con.close();
		
		System.out.println(System.currentTimeMillis() - time);
	}
	
}
