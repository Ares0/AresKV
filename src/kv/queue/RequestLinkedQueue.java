package kv.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import kv.db.Request;

/**
 * queue
 * */
public class RequestLinkedQueue<K, V> implements RequestQueue<K, V> {
	
	private ConcurrentLinkedQueue<Request<K, V>> comQueue;
	
	public RequestLinkedQueue() {
		comQueue = new ConcurrentLinkedQueue<>();
	}
	
	public void produce(Request<K, V> com) {
		comQueue.offer(com);
	}
	
	public Request<K, V> consume() {
		return comQueue.poll();
	}
	
}
