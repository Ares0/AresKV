package kv.queue;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import kv.db.Response;

/*
 *  ArrayQueue
 * 响应是按请求顺序进行，所以使用ArrayDeque顺序处理。
 * **/
public class ResponseArrayQueue implements ResponseQueue {

	private Map<Long, ArrayDeque<Response<String, String>>> resopnses;
	
	public ResponseArrayQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(Response<String, String> rep) {
		long cid = rep.getClientId();
		ArrayDeque<Response<String, String>> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new ArrayDeque<>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
		
//		System.out.println("reponse produce " + rep.getKey());
	}

	@Override
	public Response<String, String> consume(long cid) {
		ArrayDeque<Response<String, String>> reps = resopnses.get(cid);
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
