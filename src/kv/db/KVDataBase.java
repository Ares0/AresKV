package kv.db;

import kv.Command;
import kv.db.handler.DataHandler;
import kv.db.handler.ExpireHandler;
import kv.db.handler.Handler;
import kv.db.handler.WatchHandler;
import kv.db.util.KVMap;
import kv.db.util.NodeFacade;
import kv.net.Connector;
import kv.net.KVConnection;
import kv.persistence.Dumper;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestQueue;
import kv.queue.ResponseLinkedQueue;
import kv.queue.ResponseQueue;
import kv.synchro.SpinSynchronous;
import kv.synchro.Synchronous;

/**
 *  database
 * */
public class KVDataBase<K, V> implements Runnable{

	@SuppressWarnings("rawtypes")
	private static KVDataBase db;
	
	private KVConnection<K, V> con;
	
	private RequestQueue<K, V> requests;
	
	private ResponseQueue<K, V> responses;
	
	private Thread dbTh;
	
	private boolean isRunning;
	
	private long clientId;
	
	private int spinCount;
	
	private Handler<K, V> handler;
	
	private Dumper<K, V> dump;
	
	private Synchronous syn;
	
	private Connector<K, V> connector;
	
	private KVDataBase(int initCapacity) {
		this(initCapacity, new RequestLinkedQueue<K, V>(), new ResponseLinkedQueue<K, V>(), new SpinSynchronous());
	}
	
	private KVDataBase(int initCapacity, RequestQueue<K, V> req,
			ResponseQueue<K, V> rep, Synchronous syn) {
		clientId = 1;
		dump = new Dumper<>(this);
		connector = new Connector<>(this);
		
		this.syn = syn;
		this.requests = req;
		this.responses = rep;
		
		this.isRunning = true;
	}

	@SuppressWarnings("rawtypes")
	public static KVDataBase getDatabase() {
		return getDatabase(KVMap.DEFAULT_INITIAL_CAPACITY, null, null, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
	
	public KVConnection<K, V> getConnection() {
		return this.getConnection(null);
	}
	
	public KVConnection<K, V> getConnection(Synchronous syn) {
		if (con != null) {
			return con;
		}
		synchronized (this) {
			if (con == null) {
				if (syn == null) {
					con = new KVConnection<K, V>(this);
				} else {
					con = new KVConnection<K, V>(this, syn);
				}
			}
		}
		return con;
	}
	
	public RequestQueue<K, V> getRequestQueue() {
		return this.requests;
	}
	
	public ResponseQueue<K, V> getResponseQueue() {
		return this.responses;
	}

	public long getClientId() {
		return clientId++;
	}

	public Iterator getIterator() {
		return new Iterator();
	}
	
	public void start() throws InterruptedException {
		prepareHandlers();
		
		dbTh = new Thread(this);
		dbTh.setName("db-thread");
		dbTh.start();
		
		connector.start();
		dump.start();
		System.out.println("db start");
	}
	
	private void prepareHandlers() {
		handler = new ExpireHandler<>();
		handler.setDataBase(this);
		
		WatchHandler<K, V> wh = new WatchHandler<>();
		wh.setDataBase(this);
		handler.setNextHandler(wh);
		
		DataHandler<K, V> dh = new DataHandler<>();
		dh.setDataBase(this);
		wh.setNextHandler(dh);
	}
	
	public void stop() {
		dump.stop();
		connector.stop();
		
		requests = null;
		responses = null;
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			DbRequest<K, V> req;
			while ((req = requests.consume()) == null) {
				syn.doSynchronous();
				spinCount++;
			}
			
			if (req.getCommand() == Command.PUT || req.getCommand() == Command.GET
					|| req.getCommand() == Command.REMOVE || req.getCommand() == Command.RESET) {
				handler.process(req);
			} else if (req.getCommand() == Command.CLOSE) {
				handler.process(req);
				this.stop();
			} else {
				System.out.println("wrong request type " + req.getCommand());
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
		
		public NodeFacade<K, V> next() {
			index++;
			return handler.next(index);
		}
	}
	
}
