package kv.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import kv.db.Request;

/**
 * queue
 * */
public class RequestLinkedQueue implements RequestQueue{
	
	private ConcurrentLinkedQueue<Request<String, String>> comQueue;
	
	public RequestLinkedQueue() {
		comQueue = new ConcurrentLinkedQueue<>();
	}
	
	public void produce(Request<String, String> com) {
		comQueue.offer(com);
	}
	
	public Request<String, String> consume() {
		return comQueue.poll();
	}
	
}
