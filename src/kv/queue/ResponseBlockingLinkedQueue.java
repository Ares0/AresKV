package kv.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import kv.bean.DbResponse;

// LinkedBlockingQueue
public class ResponseBlockingLinkedQueue implements ResponseQueue {

	private Map<Long, BlockingQueue<DbResponse>> resopnses;
	
	public ResponseBlockingLinkedQueue() {
		resopnses = new HashMap<>();
	}
	
	@Override
	public void produce(DbResponse rep) {
		long cid = rep.getClientId();
		BlockingQueue<DbResponse> reps = resopnses.get(cid);
		if (reps == null) {
			reps = new LinkedBlockingQueue<DbResponse>();
			resopnses.put(cid, reps);
		}
		reps.offer(rep);
	}

	@Override
	public DbResponse consume(long cid) {
		BlockingQueue<DbResponse> reps = resopnses.get(cid);
		if (reps == null) {
			return null;
		}
		
		if (reps.size() > 0) {
			DbResponse rep = reps.poll();
			return rep;
		} else {
			return null;
		}
	}

}
