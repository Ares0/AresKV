package kv.db;

import kv.KVDataBase;
import kv.db.handler.Handler;
import kv.db.log.Dumper;
import kv.net.Connector;
import kv.net.KVConnection;
import kv.queue.RequestQueue;
import kv.queue.ResponseQueue;
import kv.synchro.Synchronous;
import kv.utils.KVObject;
import kv.utils.NodeFacade;

public abstract class AbstractDB implements KVDataBase {

	protected KVConnection con;
	
	protected RequestQueue requests;
	
	protected ResponseQueue responses;
	
	protected Thread dbTh;
	
	protected boolean isRunning;
	
	protected long clientId;
	
	protected int spinCount;
	
	protected Handler handler;
	
	protected Dumper dump;
	
	protected Synchronous syn;
	
	protected Connector connector;
	
	public KVConnection getConnection() {
		return this.getConnection(null);
	}
	
	public KVConnection getConnection(Synchronous syn) {
		if (con != null) {
			return con;
		}
		synchronized (this) {
			if (con == null) {
				if (syn == null) {
					con = new KVConnection(this);
				} else {
					con = new KVConnection(this, syn);
				}
			}
		}
		return con;
	}
	
	public RequestQueue getRequestQueue() {
		return this.requests;
	}
	
	public ResponseQueue getResponseQueue() {
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
	
	protected abstract void prepareHandlers(); 

	public void stop() {
		dump.stop();
		connector.stop();
		
		requests = null;
		responses = null;
		isRunning = false;
	}
	
	// iterator
	public class Iterator {
		
		private int index;
		
		public boolean hasNext() {
			index++;
			return handler.hasNext(index);
		}
		
		public NodeFacade<String, KVObject> next() {
			index++;
			return handler.next(index);
		}
	}

}
