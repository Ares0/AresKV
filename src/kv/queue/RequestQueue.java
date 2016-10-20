package kv.queue;

import kv.db.DbRequest;

public interface RequestQueue {

	public void produce(DbRequest req);
	
	public DbRequest consume();
	
}
