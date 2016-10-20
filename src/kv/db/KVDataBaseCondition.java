package kv.db;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import kv.KVConnectionCondition;
import kv.db.KVMap.Node;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestQueue;
import kv.queue.ResponseArrayQueue;
import kv.queue.ResponseQueue;
import kv.synchro.SleepSynchronous;
import kv.synchro.Synchronous;

/**
 *  database
 * */
public class KVDataBaseCondition implements Runnable{

	private static KVDataBaseCondition db;
	
	private KVMap<String, String>[] dt;
	
	private int rehashIndex;
	
	private boolean isRehash;
	
	private RequestQueue requests;
	
	private ResponseQueue responses;
	
	private Thread dbTh;
	
	private boolean isRunning;
	
	private Synchronous syn;
	
	private long clientId;
	
	private int spinCount;
	
	private Lock lock;
	
	public Condition reqCondition;
	
	public Condition repCondition;
	
	private KVDataBaseCondition(int initCapacity) {
		this(initCapacity, new RequestLinkedQueue(), new ResponseArrayQueue(), new SleepSynchronous());
	}
	
	@SuppressWarnings("unchecked")
	private KVDataBaseCondition(int initCapacity, RequestQueue req, ResponseQueue rep, Synchronous syn) {
		clientId = 1;
		rehashIndex = -1;
		
		dt = new KVMap[2];
		dt[0] = new KVMap<>(initCapacity);
		
		this.syn = syn;
		this.requests = req;
		this.responses = rep;
		
		lock = new ReentrantLock();
		reqCondition = lock.newCondition();
		repCondition = lock.newCondition();
		
		this.isRunning = true;
		this.isRehash = false;
		
		this.start();
	}

	public static KVDataBaseCondition getDataBase() {
		return getDataBase(KVMap.DEFAULT_INITIAL_CAPACITY, null, null, null);
	}
	
	// getDatabase
	public static KVDataBaseCondition getDataBase(int initCapacity, RequestQueue req, ResponseQueue rep, Synchronous syn) {
		if (db != null) {
			return db;
		}
		synchronized (KVDataBaseCondition.class) {
			if (db == null) {
				if (req == null || syn == null) {
					db = new KVDataBaseCondition(initCapacity);
				} else {
					db = new KVDataBaseCondition(initCapacity, req, rep, syn);
				}
			}
		}
		return db;
	}
	
	public RequestQueue getRequestQueue() {
		return this.requests;
	}
	
	public ResponseQueue getResponseQueue() {
		return this.responses;
	}
	
	private int reqWaitCount;
	
	private int reqSignalCount;
	
	private int repWaitCount;
	
	private int repSignalCount;
	
	public void doReqWait() {
		try {
			synchronized (reqCondition) {
//				reqCondition.await();
				reqCondition.wait();
				reqWaitCount++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void doReqSignal() {
		synchronized (reqCondition) {
//			reqCondition.signal();
			reqCondition.notify();
			reqSignalCount++;
		}
	}
	
	public void doRepWait() {
		try {
			synchronized (repCondition) {
//				repCondition.await();
				repCondition.wait();
				repWaitCount++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void doRepSignal() {
		synchronized (repCondition) {
//			repCondition.signalAll();
			repCondition.notify();
			repSignalCount++;
		}
	}
	
	// Connection
	public KVConnectionCondition getConnection() {
		KVConnectionCondition con = new KVConnectionCondition(this);
		return con;
	}
	
	public KVConnectionCondition getConnection(Synchronous syn) {
		KVConnectionCondition con = new KVConnectionCondition(this, syn);
		return con;
	}

	// put rehash
	private void put(String key, String value) {
		if (!isRehash) {
			if (dt[0].resize()) {
				isRehash = true;
				rehashIndex = 0;
				resize();
			}
		}
		
		if (isRehash) {
			dt[1].put(key, value);
			rehash();
		} else {
			dt[0].put(key, value);
		}
	}

	private void resize() {
		int capacity = dt[0].capacity() << 1;
		if (capacity >= Integer.MAX_VALUE) {
			throw new ArrayIndexOutOfBoundsException();
		}
		capacity = capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : capacity;
		dt[1] = new KVMap<>(capacity);
	}

	// get dt1最新
	private void get(String key, long cid) {
		String value;
		
		if (isRehash) {
			if ((value = dt[1].get(key)) == null) {
				value = dt[0].get(key);
			} 
			rehash();
		} else {
			value = dt[0].get(key);
		}
		
		produceResponse(key, cid, value);
		
		doRepSignal();
//		System.out.println("rep signal " + key);
	}

	private void produceResponse(String key, long cid, String value) {
		Response<String, String> rep = new Response<>();
		if (value == null) {
			rep.setClientId(cid);
			rep.setKey(key);
		} else {
			rep.setClientId(cid);
			rep.setKey(key);
			rep.setValue(value);
		}
		responses.produce(rep);
	}
	
	// remove
	private void remove(String key) {
		if (isRehash) {
			dt[0].remove(key);
			dt[1].remove(key);
			rehash();
		} else {
			dt[0].remove(key);
		}
	}
	
	private void rehash() {
		if (rehashIndex < dt[0].capacity()) {
			Node<String, String> e = dt[0].getIndex(rehashIndex);
			if (e != null) {
				dt[1].putNode(e);
			}
			rehashIndex++;
		} else {
			dt[0] = dt[1];
			dt[1] = null;
			
			isRehash = false;
			rehashIndex = -1;
		}
	}

	// reset
	private void reset() {
		dt[0] = null;
		dt[1] = null;
		dt = null;
	}
	
	// dt[0]
	public Node<String, String>[] getNodes() {
		// 返回拷贝，会增大内存
		// 也可以直接返回，但不符合单一职责
		return dt[0].getNodes();
	}

	private void start() {
		dbTh = new Thread(this);
		dbTh.setName("db-thread");
		dbTh.start();
		
		System.out.println("db start");
	}
	
	private void stop() {
		dt = null;
		requests = null;
		responses = null;
		
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			Request<String, String> req;
			while ((req = requests.consume()) == null) {
				syn.doSynchronous();
				spinCount++;
			}
			
			System.out.println(reqWaitCount + "-" + reqSignalCount);
			System.out.println(repWaitCount + "-" + repSignalCount);
			
			if (req.getType() == Request.PUT) {
				this.put(req.getKey(), req.getValue());
			} else if (req.getType() == Request.GET) {
				this.get(req.getKey(), req.getClientId());
			} else if (req.getType() == Request.REMOVE) {
				this.remove(req.getKey());
			} else if (req.getType() == Request.RESET) {
				this.reset();
			} else if (req.getType() == Request.CLOSE) {
				this.stop();
			} else {
				System.out.println("wrong request" + req.getType());
			}
		}
		System.out.println("db stop " + spinCount);
	}

	public long getClientId() {
		return clientId++;
	}
	
}
