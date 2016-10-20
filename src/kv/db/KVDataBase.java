package kv.db;

import kv.KVConnection;
import kv.db.KVMap.Node;
import kv.persistence.Dumper;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestQueue;
import kv.queue.ResponseArrayQueue;
import kv.queue.ResponseQueue;
import kv.synchro.SleepSynchronous;
import kv.synchro.Synchronous;

/**
 *  database
 * */
public class KVDataBase implements Runnable{

	private static KVDataBase db;
	
	private KVMap<String, String>[] dt;
	
	private int rehashIndex;
	
	private boolean isRehash;
	
	private Dumper dump;
	
	private RequestQueue requests;
	
	private ResponseQueue responses;
	
	private Thread dbTh;
	
	private boolean isRunning;
	
	private Synchronous syn;
	
	private long clientId;
	
	private int spinCount;
	
	private KVDataBase(int initCapacity) {
		this(initCapacity, new RequestLinkedQueue(), new ResponseArrayQueue(), new SleepSynchronous());
	}
	
	@SuppressWarnings("unchecked")
	private KVDataBase(int initCapacity, RequestQueue req, ResponseQueue rep, Synchronous syn) {
		clientId = 1;
		rehashIndex = -1;
		
		dt = new KVMap[2];
		dt[0] = new KVMap<>(initCapacity);
		dump = new Dumper(this);
		
		this.syn = syn;
		this.requests = req;
		this.responses = rep;
		
		this.isRunning = true;
		this.isRehash = false;
		
		this.start();
	}

	public static KVDataBase getDatabase() {
		return getDatabase(KVMap.DEFAULT_INITIAL_CAPACITY, null, null, null);
	}
	
	// getDatabase
	public static KVDataBase getDatabase(int initCapacity, RequestQueue req, ResponseQueue rep, Synchronous syn) {
		if (db != null) {
			return db;
		}
		synchronized (KVDataBase.class) {
			if (db == null) {
				if (req == null || syn == null) {
					db = new KVDataBase(initCapacity);
				} else {
					db = new KVDataBase(initCapacity, req, rep, syn);
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
	
	// Connection
	public KVConnection getConnection() {
		KVConnection con = new KVConnection(this);
		return con;
	}
	
	public KVConnection getConnection(Synchronous syn) {
		KVConnection con = new KVConnection(this, syn);
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

	// get dt1����
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
		
		// send response
		produceResponse(key, cid, value);
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
		// ���ؿ������������ڴ�
		// Ҳ����ֱ�ӷ��أ��������ϵ�һְ��
		return dt[0].getNodes();
	}

	private void start() {
		dbTh = new Thread(this);
		dbTh.setName("db-thread");
		dbTh.start();
		
		dump.start();
		System.out.println("db start");
	}
	
	private void stop() {
		dump.stop();
		
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