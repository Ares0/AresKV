package kv.queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kv.db.DbResponse;

// linkedlist
public class ResponseLinkedQueue<K, V> implements ResponseQueue<K, V> {

	private Map<Long, LinkedList<DbResponse<K, V>>> resopnses;
	
	public ResponseLinkedQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(DbResponse<K, V> rep) {
		long cid = rep.getClientId();
		LinkedList<DbResponse<K, V>> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new LinkedList<>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
		
//		System.out.println("reponse produce " + rep.getKey());
	}

	@Override
	public DbResponse<K, V> consume(long cid) {
		LinkedList<DbResponse<K, V>> reps = resopnses.get(cid);
		if (reps == null) {
			return null;
		}
		
		if (reps.size() > 0) {
			DbResponse<K, V> rep = reps.poll();
//			System.out.println("reponse consume " + rep.getKey());
			return rep;
		} else {
			return null;
		}
	}

}
