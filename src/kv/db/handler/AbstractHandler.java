package kv.db.handler;

import kv.KVDataBase;
import kv.utils.KVObject;
import kv.utils.NodeFacade;

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
	public NodeFacade<String, KVObject> next(int index) {
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
		return true;
	}

	@Override
	public void expire(String key) {
		next.expire(key);
	}

}
