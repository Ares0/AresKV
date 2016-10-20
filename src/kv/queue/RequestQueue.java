package kv.queue;

import kv.bean.DbRequest;

public interface RequestQueue {

	public void produce(DbRequest req);
	
	public DbRequest consume();
	
}
