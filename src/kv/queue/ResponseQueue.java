package kv.queue;

import kv.db.Response;

public interface ResponseQueue<K, V> {

	public void produce(Response<K, V> rep);
	
	public Response<K, V> consume(long cid);

}
