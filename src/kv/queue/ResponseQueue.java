package kv.queue;

import kv.bean.DbResponse;

public interface ResponseQueue {

	public void produce(DbResponse rep);
	
	public DbResponse consume(long cid);

}
