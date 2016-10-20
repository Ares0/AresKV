package kv;

import kv.db.KVDataBase;
import kv.db.Request;
import kv.db.Response;
import kv.synchro.SleepSynchronous;
import kv.synchro.Synchronous;

/**
 *  Connection
 * */
public class KVConnection {
	
	private final KVDataBase db;
	
	private ThreadLocal<Long> reps;
	
	private Synchronous syn;
	
	private int spinCount;
	
	public KVConnection(KVDataBase db) {
		this(db, new SleepSynchronous());
	}
	
	public KVConnection(KVDataBase db, Synchronous syn) {
		this.db = db;
		this.syn = syn;
		this.reps = new ThreadLocal<>();
	}

	public void put(String key, String value) {
		db.getRequestQueue().produce(new Request<String, String>(Request.PUT, key, value, getClientId()));
	}
	
	public void put(String key, String value, long expire) {
		Request<String, String> req = new Request<String, String>(Request.PUT, key, value, getClientId());
		req.setExpireTime(expire);
		db.getRequestQueue().produce(req);
	}
	
	public void put(String key, String value, boolean watch) {
		Request<String, String> req = new Request<String, String>(Request.PUT, key, value, getClientId());
		req.setWatch(watch);
		db.getRequestQueue().produce(req);
	}
	
	public void put(String key, String value, long expire, boolean watch) {
		Request<String, String> req = new Request<String, String>(Request.PUT, key, value, getClientId());
		req.setExpireTime(expire);
		req.setWatch(watch);
		db.getRequestQueue().produce(req);
	}
	
	//  一直到有值产生
	// 保存Thread对用的clientId
	public String get(String key) {
		long clientId = getClientId();
		db.getRequestQueue().produce(new Request<String, String>(Request.GET, key, null, clientId));
		
		Response<String, String> value;
		while ((value = db.getResponseQueue().consume(clientId)) == null) {
			syn.doSynchronous();
			spinCount++;
		}
		
		return value.getValue();
	}
	
	public void remove(String key) {
		db.getRequestQueue().produce(new Request<String, String>(Request.GET, key, null, getClientId()));
	}
	
	public void reset() {
		db.getRequestQueue().produce(new Request<String, String>(Request.RESET, null, null, getClientId()));
	}
	
	public void close() {
		System.out.println("connection spin " + spinCount);
		db.getRequestQueue().produce(new Request<String, String>(Request.CLOSE, null, null, getClientId()));
	}
	
	// thread的clientId
	private long getClientId() {
		Long clientId ;
		if ((clientId = reps.get()) == null) {
			reps.set((clientId = db.getClientId()));
		}
		return clientId;
	}
	
}
