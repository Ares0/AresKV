package kv.db;

import java.util.List;
import java.util.Map;

import kv.KVDataBase;
import kv.db.MasterSlaveDB.DBState;
import kv.queue.RequestQueue;
import kv.queue.ResponseQueue;
import kv.synchro.Synchronous;
import kv.utils.KVMap;
import kv.utils.Range;

// Factory
public class DBFactory {

	private static KVDataBase db;
	
	public static StandAloneDB getStandardDB() {
		return getStandardDB(KVMap.DEFAULT_INITIAL_CAPACITY, null, null, null);
	}
	
	public static synchronized StandAloneDB getStandardDB(int initCapacity, RequestQueue req, 
			ResponseQueue rep, Synchronous syn) {
		if (db != null) {
			return (StandAloneDB) db;
		} else {
			if (req == null || syn == null) {
				db = new StandAloneDB(initCapacity);
			} else {
				db = new StandAloneDB(initCapacity, req, rep, syn);
			}
		}
		return (StandAloneDB) db;
	}
	
	public static ClusterDB getClusterDB(int start, int end) {
		return getClusterDB(start, end, null, null, null, null);
	}
	
	public static synchronized ClusterDB getClusterDB(int start, int end, RequestQueue req, 
			ResponseQueue rep, Synchronous syn, Map<String, Range> clusterRange) {
		if (db != null) {
			return (ClusterDB) db;
		} else {
			if (req == null || syn == null) {
				db = new ClusterDB(start, end);
			} else {
				db = new ClusterDB(start, end, req, rep, syn, clusterRange);
			}
		}
		return (ClusterDB) db;
	}
	
	public static MasterSlaveDB getMasterSlaveDB() {
		return getMasterSlaveDB(null, null, null, null, null, null);
	}
	
	public static synchronized MasterSlaveDB getMasterSlaveDB(RequestQueue req, 
			ResponseQueue rep, Synchronous syn, RequestQueue dup, List<String> sa, DBState state) {
		if (db != null) {
			return (MasterSlaveDB) db;
		} else {
			if (req == null || syn == null) {
				db = new MasterSlaveDB();
			} else {
				db = new MasterSlaveDB(req, rep, syn, dup, sa, state);
			}
		}
		return (MasterSlaveDB) db;
	}
}
