package kv.db;

import kv.KVConnection;
import kv.db.handler.DataHandler;
import kv.db.handler.ExpireHandler;
import kv.db.handler.Handler;
import kv.db.handler.WatchHandler;
import kv.db.util.KVMap;
import kv.db.util.NodeFacade;
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
	
	private RequestQueue<String, String> requests;
	
	private ResponseQueue<String, String> responses;
	
	private Thread dbTh;
	
	private boolean isRunning;
	
	private long clientId;
	
	private int spinCount;
	
	private Handler<String, String> handler;
	
	private Dumper dump;
	
	private Synchronous syn;
	
	private KVDataBase(int initCapacity) {
		this(initCapacity, new RequestLinkedQueue<String, String>(), new ResponseArrayQueue<String, String>(), new SleepSynchronous());
	}
	
	private KVDataBase(int initCapacity, RequestQueue<String, String> req,
			ResponseQueue<String, String> rep, Synchronous syn) {
		clientId = 1;
		dump = new Dumper(this);
		
		this.syn = syn;
		this.requests = req;
		this.responses = rep;
		
		this.isRunning = true;
		this.start();
	}

	public static KVDataBase getDatabase() {
		return getDatabase(KVMap.DEFAULT_INITIAL_CAPACITY, null, null, null);
	}
	
	public static KVDataBase getDatabase(int initCapacity, RequestQueue<String, String> req, 
			ResponseQueue<String, String> rep, Synchronous syn) {
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
	
	public KVConnection getConnection() {
		KVConnection con = new KVConnection(this);
		return con;
	}
	
	public KVConnection getConnection(Synchronous syn) {
		KVConnection con = new KVConnection(this, syn);
		return con;
	}
	
	public RequestQueue<String, String> getRequestQueue() {
		return this.requests;
	}
	
	public ResponseQueue<String, String> getResponseQueue() {
		return this.responses;
	}

	public long getClientId() {
		return clientId++;
	}

	public Iterator getIterator() {
		return new Iterator();
	}
	
	private void start() {
		prepareHandlers();
		
		dbTh = new Thread(this);
		dbTh.setName("db-thread");
		dbTh.start();
		
		dump.start();
		System.out.println("db start");
	}
	
	private void prepareHandlers() {
		handler = new ExpireHandler<>();
		handler.setDataBase(this);
		
		WatchHandler<String, String> wh = new WatchHandler<>();
		wh.setDataBase(this);
		handler.setNextHandler(wh);
		
		DataHandler<String, String> dh = new DataHandler<>();
		dh.setDataBase(this);
		wh.setNextHandler(dh);
	}
	
	private void stop() {
		dump.stop();
		
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
			
			if (req.getType() == Request.PUT || req.getType() == Request.GET
					|| req.getType() == Request.REMOVE || req.getType() == Request.RESET) {
				handler.process(req);
			} else if (req.getType() == Request.CLOSE) {
				handler.process(req);
				this.stop();
			} else {
				System.out.println("wrong request type " + req.getType());
			}
		}
		System.out.println("db stop " + spinCount);
	}
	
	// iterator
	public class Iterator {
		
		private int index;
		
		public boolean hasNext() {
			index++;
			return handler.hasNext(index);
		}
		
		public NodeFacade<String, String> next() {
			index++;
			return handler.next(index);
		}
	}
	
}
