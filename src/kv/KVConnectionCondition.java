package kv;

import kv.db.KVDataBaseCondition;
import kv.db.Request;
import kv.db.Response;
import kv.synchro.SleepSynchronous;
import kv.synchro.Synchronous;

/**
 *  Connection
 * */
public class KVConnectionCondition {
	
	private final KVDataBaseCondition db;
	
	private ThreadLocal<Long> reps;
	
	private Synchronous syn;
	
	private int spinCount;
	
	public KVConnectionCondition(KVDataBaseCondition kvDataBaseCondition) {
		this(kvDataBaseCondition, new SleepSynchronous());
	}
	
	public KVConnectionCondition(KVDataBaseCondition kvDataBaseCondition, Synchronous syn) {
		this.db = kvDataBaseCondition;
		this.syn = syn;
		this.reps = new ThreadLocal<>();
	}

	public void put(String key, String value) {
		db.getRequestQueue().produce(new Request<String, String>(Request.PUT, key, value, getClientId()));
		db.doReqSignal();
	}
	
	//  一直到有值产生
	// 保存Thread对用的clientId
	public String get(String key) {
		long clientId = getClientId();
		db.getRequestQueue().produce(new Request<String, String>(Request.GET, key, null, clientId));
		
		db.doReqSignal();
		
		Response<String, String> value;
		while ((value = db.getResponseQueue().consume(clientId)) == null) {
			syn.doSynchronous();
//			System.out.println("rep wait " + key);
			spinCount++;
		}
		
		return value.getValue();
	}
	
	public void remove(String key) {
		db.getRequestQueue().produce(new Request<String, String>(Request.GET, key, null, getClientId()));
		db.doReqSignal();
	}
	
	public void reset() {
		db.getRequestQueue().produce(new Request<String, String>(Request.RESET, null, null, getClientId()));
		db.doReqSignal();
	}
	
	public void close() {
		System.out.println("connection spin " + spinCount);
		db.getRequestQueue().produce(new Request<String, String>(Request.CLOSE, null, null, getClientId()));
		db.doReqSignal();
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
