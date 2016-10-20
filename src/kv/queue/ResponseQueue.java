package kv.queue;

import kv.db.DbResponse;

public interface ResponseQueue {

	public void produce(DbResponse rep);
	
	public DbResponse consume(long cid);

}
