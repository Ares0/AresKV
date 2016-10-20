package kv.db.handler;

import kv.KVDataBase;
import kv.bean.DbRequest;
import kv.utils.KVObject;
import kv.utils.KVNode;

public abstract class AbstractHandler implements Handler {

	protected Handler next;
	
	protected KVDataBase db;
	
	@Override
	public void setNextHandler(Handler h) {
		this.next = h;
	}
	
	public void setDataBase(KVDataBase db) {
		this.db = db;
	}
	
	@Override
	public KVNode<String, KVObject> next(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		return next.next(index);
	}
	
	@Override
	public boolean hasNext(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		return next.hasNext(index);
	}

	@Override
	public void expire(String key) {
		next.expire(key);
	}
	
	@Override
	public void dirty(DbRequest req) {
		next.dirty(req);
	}

}
