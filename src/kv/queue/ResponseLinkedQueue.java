package kv.queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kv.db.Response;

// linkedlist
public class ResponseLinkedQueue<K, V> implements ResponseQueue<K, V> {

	private Map<Long, LinkedList<Response<K, V>>> resopnses;
	
	public ResponseLinkedQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(Response<K, V> rep) {
		long cid = rep.getClientId();
		LinkedList<Response<K, V>> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new LinkedList<>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
		
//		System.out.println("reponse produce " + rep.getKey());
	}

	@Override
	public Response<K, V> consume(long cid) {
		LinkedList<Response<K, V>> reps = resopnses.get(cid);
		if (reps == null) {
			return null;
		}
		
		if (reps.size() > 0) {
			Response<K, V> rep = reps.poll();
//			System.out.println("reponse consume " + rep.getKey());
			return rep;
		} else {
			return null;
		}
	}

}
