package kv.test.spin;

import java.util.concurrent.CountDownLatch;

import kv.KVConnectionCondition;
import kv.db.KVDataBaseCondition;
import kv.queue.RequestLinkedQueue;
import kv.queue.ResponseArrayQueue;
import kv.synchro.RepConditionSynchronous;
import kv.synchro.ReqConditionSynchronous;

public class ConcurrentConditionTest {
	
	private KVDataBaseCondition db = KVDataBaseCondition.getDataBase(16, new RequestLinkedQueue(), new ResponseArrayQueue(), new ReqConditionSynchronous());
	
	private KVConnectionCondition con = db.getConnection(new RepConditionSynchronous());
	
	private CountDownLatch latch;

	public static void main(String[] args) throws InterruptedException {
		ConcurrentConditionTest cs = new ConcurrentConditionTest();
		
		cs.concurrentReadWrite();
	}
	
	/*
	 * Array spin spin   176ms
	 * Linked spin spin   183ms
	 * **/ 
	public void concurrentReadWrite() throws InterruptedException {
		latch = new CountDownLatch(3);
		
		Thread t0 = new Thread(new write());
		t0.setName("t0");
		t0.start();
		
		Thread t1 = new Thread(new read());
		t1.setName("t1");
		t1.start();
		
		Thread t2 = new Thread(new read());
		t2.setName("t2");
		t2.start();
		
		latch.await();
		con.close();
	}

	public class write implements Runnable{
		@Override
		public void run() {
			linkedWrite();
		}
	}
	
	public class read implements Runnable{
		@Override
		public void run() {
			linkedRead();
		}
	}
	
	public void linkedWrite() {
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 100000; i++) {
			con.put("" + i, "" + i);
		}
		
		System.out.println("write time " + (System.currentTimeMillis() - time));
		
		latch.countDown();
	}
	
	public void linkedRead() {
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 100000; i++) {
			con.get("" + i);
		}
		
		System.out.println("read time " + (System.currentTimeMillis() - time));
		
		latch.countDown();
	}
	
}
