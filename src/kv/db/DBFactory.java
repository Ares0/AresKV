package kv.db;

import java.util.Map;

import kv.KVDataBase;
import kv.queue.RequestQueue;
import kv.queue.ResponseQueue;
import kv.synchro.Synchronous;
import kv.utils.KVMap;
import kv.utils.Range;

public class DBFactory {

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
	
	public static ClusterDB getClusterDB(int start, int end) {
		return getClusterDB(start, end, null, null, null, null);
	}
	
	public static ClusterDB getClusterDB(int start, int end, RequestQueue req, 
			ResponseQueue rep, Synchronous syn, Map<String, Range> clusterRange) {
		if (db != null) {
			return (ClusterDB) db;
		}
		synchronized (StandardDB.class) {
			if (db == null) {
				if (req == null || syn == null) {
					db = new ClusterDB(start, end);
				} else {
					db = new ClusterDB(start, end, req, rep, syn, clusterRange);
				}
			}
		}
		return (ClusterDB) db;
	}
}
