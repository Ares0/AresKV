package kv.db.handler;

import kv.db.KVDataBase;
import kv.db.Request;
import kv.db.util.NodeFacade;

// handler
public interface Handler<K, V> {

	void process(Request<K, V> req);
	
	void setNextHandler(Handler<K, V> h);
	
	void setDataBase(KVDataBase db);
	
	boolean hasNext(int index);
	
	NodeFacade<K, V> next(int index);
	
	void expire(K key);
	
}
