package kv.db.handler;

import kv.db.KVDataBase;
import kv.db.DbRequest;
import kv.db.util.NodeFacade;

// handler
public interface Handler<K, V> {

	void process(DbRequest<K, V> req);
	
	void setNextHandler(Handler<K, V> h);
	
	void setDataBase(KVDataBase<K, V> kvDataBase);
	
	boolean hasNext(int index);
	
	NodeFacade<K, V> next(int index);
	
	void expire(K key);
	
}
