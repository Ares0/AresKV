package kv.queue;

import kv.db.Request;

public interface RequestQueue {

	public void produce(Request<String, String> req);
	
	public Request<String, String> consume();
	
}
