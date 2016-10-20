package kv.db;

import kv.KVDataBase;
import kv.queue.RequestQueue;
import kv.queue.ResponseQueue;
import kv.synchro.Synchronous;
import kv.utils.KVMap;

public class DataBaseFactory {

	private static KVDataBase db;
	
	public static StandardDB getStandardDB() {
		return getStandardDB(KVMap.DEFAULT_INITIAL_CAPACITY, null, null, null);
	}
	
	public static StandardDB getStandardDB(int initCapacity, RequestQueue req, 
			ResponseQueue rep, Synchronous syn) {
		if (db != null) {
			return (StandardDB) db;
		}
		synchronized (StandardDB.class) {
			if (db == null) {
				if (req == null || syn == null) {
					db = new StandardDB(initCapacity);
				} else {
					db = new StandardDB(initCapacity, req, rep, syn);
				}
			}
		}
		return (StandardDB) db;
	}
}
