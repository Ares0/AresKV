package kv.queue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import kv.db.DbResponse;

/*
 *  ArrayQueue
 * 响应按请求顺序，可以顺序处理；
 * doubleCapacity可能造成内存过大。
 * **/
public class ResponseArrayQueue implements ResponseQueue {

	private Map<Long, ArrayDeque<DbResponse>> resopnses;
	
	public ResponseArrayQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(DbResponse rep) {
		long cid = rep.getClientId();
		ArrayDeque<DbResponse> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new ArrayDeque<>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
		
//		System.out.println("reponse produce " + rep.getKey());
	}

	@Override
	public DbResponse consume(long cid) {
		ArrayDeque<DbResponse> reps = resopnses.get(cid);
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
