package kv.queue;

import kv.db.Request;

public interface RequestQueue<K, V> {

	public void produce(Request<K, V> req);
	
	public Request<K, V> consume();
	
}
