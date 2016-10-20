package kv.db.handler;

import kv.db.KVDataBase;
import kv.db.util.NodeFacade;

public abstract class AbstractHandler<K, V> implements Handler<K, V> {

	protected Handler<K, V> next;
	
	protected KVDataBase<K, V> db;
	
	@Override
	public void setNextHandler(Handler<K, V> h) {
		this.next = h;
	}
	
	public void setDataBase(KVDataBase<K, V> db) {
		this.db = db;
	}
	
	@Override
	public NodeFacade<K, V> next(int index) {
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
	public void expire(K key) {
		next.expire(key);
	}

}
