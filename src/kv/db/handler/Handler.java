package kv.db.handler;

import kv.utils.KVObject;
import kv.utils.KVNode;
import kv.KVDataBase;
import kv.bean.DbRequest;

// handler
public interface Handler {

	void process(DbRequest req);
	
	void setNextHandler(Handler h);
	
	void setDataBase(KVDataBase kvDataBase);
	
	boolean hasNext(int index);
	
	KVNode<String, KVObject> next(int index);
	
	void expire(String key);

	void dirty(DbRequest req);
	
}
