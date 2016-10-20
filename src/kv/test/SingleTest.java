package kv.test;

import kv.KVConnection;
import kv.db.KVDataBase;
import kv.queue.RequestLinkedQueue;
import kv.queue.ResponseArrayQueue;
import kv.synchro.SpinSynchronous;

public class SingleTest {

	private static KVDataBase db = KVDataBase.getDatabase(16, new RequestLinkedQueue<String, String>(), 
			new ResponseArrayQueue<String, String>(), new SpinSynchronous());
	
	private static KVConnection con = db.getConnection(new SpinSynchronous());
	
	public static void main(String[] args) throws InterruptedException {
		watchTest();
	}
	
	public static void expireTest() throws InterruptedException {
		con.put("" + 1, "" + 1, 100000000);
		System.out.println(con.get("" + 1));
		con.close();
	}
	
	public static void watchTest() {
		con.put("" + 1, "" + 1, true);
		con.put("" + 1, "" + 2);
		System.out.println(con.get("" + 1));
		con.close();
	}

}
