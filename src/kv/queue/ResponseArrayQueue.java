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
public class ResponseArrayQueue<K, V> implements ResponseQueue<K, V> {

	private Map<Long, ArrayDeque<DbResponse<K, V>>> resopnses;
	
	public ResponseArrayQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(DbResponse<K, V> rep) {
		long cid = rep.getClientId();
		ArrayDeque<DbResponse<K, V>> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new ArrayDeque<>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
		
//		System.out.println("reponse produce " + rep.getKey());
	}

	@Override
	public DbResponse<K, V> consume(long cid) {
		ArrayDeque<DbResponse<K, V>> reps = resopnses.get(cid);
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
