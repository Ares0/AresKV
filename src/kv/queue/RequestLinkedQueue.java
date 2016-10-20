package kv.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import kv.db.DbRequest;

/**
 * queue
 * */
public class RequestLinkedQueue implements RequestQueue {
	
	private ConcurrentLinkedQueue<DbRequest> comQueue;
	
	public RequestLinkedQueue() {
		comQueue = new ConcurrentLinkedQueue<>();
	}
	
	public void produce(DbRequest com) {
		comQueue.offer(com);
	}
	
	public DbRequest consume() {
		return comQueue.poll();
	}
	
}
