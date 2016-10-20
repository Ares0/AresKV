package kv.queue;

import kv.db.DbResponse;

public interface ResponseQueue<K, V> {

	public void produce(DbResponse<K, V> rep);
	
	public DbResponse<K, V> consume(long cid);

}
