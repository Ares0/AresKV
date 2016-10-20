package kv.queue;

import kv.db.DbRequest;

public interface RequestQueue<K, V> {

	public void produce(DbRequest<K, V> req);
	
	public DbRequest<K, V> consume();
	
}
