package kv.test;

import java.util.concurrent.CountDownLatch;

import kv.KVConnection;
import kv.db.KVDataBase;
import kv.queue.RequestLinkedQueue;
import kv.queue.ResponseArrayQueue;
import kv.synchro.SleepSynchronous;
import kv.synchro.SpinSynchronous;

public class ConcurrentSleepTest {
	
	private KVDataBase db = KVDataBase.getDatabase(16, new RequestLinkedQueue(), new ResponseArrayQueue(), new SpinSynchronous());
	
	private KVConnection con = db.getConnection(new SleepSynchronous(5));
	
	private CountDownLatch latch;

	public static void main(String[] args) throws InterruptedException {
		ConcurrentSleepTest cs = new ConcurrentSleepTest();
		
//		cs.concurrentWrite();
		
//		cs.concurrentReadWrite();
		
		cs.concurrentReadWrite2();
	}
	
	// 151ms
	public void concurrentWrite() throws InterruptedException {
		latch = new CountDownLatch(3);
		
		Thread t0 = new Thread(new write());
		t0.setName("t0");
		t0.start();
		
		Thread t1 = new Thread(new write());
		t1.setName("t1");
		t1.start();
		
		Thread t2 = new Thread(new write());
		t2.setName("t2");
		t2.start();
		
		latch.await();
		con.close();
	}
	
	// 100344ms
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
	
	// 100340ms
	public void concurrentReadWrite2() throws InterruptedException {
		latch = new CountDownLatch(3);
		
		Thread t0 = new Thread(new write());
		t0.setName("t0");
		t0.start();
		
		Thread t1 = new Thread(new write());
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
		
		System.out.println(System.currentTimeMillis() - time);
		
		latch.countDown();
	}
	
	public void linkedRead() {
		long time = System.currentTimeMillis();

		for (int i = 0; i < 100000; i++) {
			con.get("" + i);
		}
		
		System.out.println(System.currentTimeMillis() - time);
		
		latch.countDown();
	}
	
}
