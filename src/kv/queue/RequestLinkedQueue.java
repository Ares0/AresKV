package kv.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import kv.db.DbRequest;

/**
 * queue
 * */
public class RequestLinkedQueue<K, V> implements RequestQueue<K, V> {
	
	private ConcurrentLinkedQueue<DbRequest<K, V>> comQueue;
	
	public RequestLinkedQueue() {
		comQueue = new ConcurrentLinkedQueue<>();
	}
	
	public void produce(DbRequest<K, V> com) {
		comQueue.offer(com);
	}
	
	public DbRequest<K, V> consume() {
		return comQueue.poll();
	}
	
}
