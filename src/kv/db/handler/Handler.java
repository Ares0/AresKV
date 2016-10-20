package kv.db.handler;

import kv.utils.KVObject;
import kv.utils.NodeFacade;
import kv.KVDataBase;
import kv.db.DbRequest;

// handler
public interface Handler {

	void process(DbRequest req);
	
	void setNextHandler(Handler h);
	
	void setDataBase(KVDataBase kvDataBase);
	
	boolean hasNext(int index);
	
	NodeFacade<String, KVObject> next(int index);
	
	void expire(String key);
	
}
