package kv.queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kv.db.Response;

// linkedlist
public class ResponseLinkedQueue implements ResponseQueue {

	private Map<Long, LinkedList<Response<String, String>>> resopnses;
	
	public ResponseLinkedQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(Response<String, String> rep) {
		long cid = rep.getClientId();
		LinkedList<Response<String, String>> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new LinkedList<>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
		
//		System.out.println("reponse produce " + rep.getKey());
	}

	@Override
	public Response<String, String> consume(long cid) {
		LinkedList<Response<String, String>> reps = resopnses.get(cid);
		if (reps == null) {
			return null;
		}
		
		if (reps.size() > 0) {
			Response<String, String> rep = reps.poll();
//			System.out.println("reponse consume " + rep.getKey());
			return rep;
		} else {
			return null;
		}
	}

}
