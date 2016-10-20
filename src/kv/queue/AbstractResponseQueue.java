package kv.queue;

import kv.bean.DbResponse;

public abstract class AbstractResponseQueue implements ResponseQueue {

	@Override
	public void produce(DbResponse rep) {
		throw new AbstractMethodError();
	}

	@Override
	public DbResponse consume(long cid) {
		throw new AbstractMethodError();
	}

	@Override
	public DbResponse consume(String key) {
		throw new AbstractMethodError();
	}

}
