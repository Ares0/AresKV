package kv.queue;

import kv.db.Response;

public interface ResponseQueue {

	public void produce(Response<String, String> rep);
	
	public Response<String, String> consume(long cid);
	
}
