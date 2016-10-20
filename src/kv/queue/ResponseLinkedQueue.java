package kv.queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kv.bean.DbResponse;

// linkedlist
public class ResponseLinkedQueue extends AbstractResponseQueue {

	private Map<Long, LinkedList<DbResponse>> resopnses;
	
	public ResponseLinkedQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(DbResponse rep) {
		long cid = rep.getClientId();
		LinkedList<DbResponse> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new LinkedList<>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
		
//		System.out.println("reponse produce " + rep.getKey());
	}

	@Override
	public DbResponse consume(long cid) {
		LinkedList<DbResponse> reps = resopnses.get(cid);
		if (reps == null) {
			return null;
		}
		
		if (reps.size() > 0) {
			DbResponse rep = reps.poll();
//			System.out.println("reponse consume " + rep.getKey());
			return rep;
		} else {
			return null;
		}
	}

}
