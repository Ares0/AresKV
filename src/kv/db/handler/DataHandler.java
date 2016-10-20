package kv.db.handler;

import kv.Command;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.utils.DataTable;
import kv.utils.KVNode;
import kv.utils.KVMap.Node;
import kv.utils.KVObject;

/**
 *  DataHandler
 * */ 
public class DataHandler extends AbstractHandler {

	private DataTable<String, KVObject> dt;
	
	private final KVNode<String, KVObject> none = new KVNode<>(0, (String)null, (KVObject)null, null, 0);
	
	public DataHandler() {
		dt = new DataTable<>();
	}
	
	public void process(DbRequest req) {
		int type = req.getCommand();
		
		if (type == Command.PUT) {
			dt.put(req.getKey(), req.getValue(), req.getClientId());
			produceRepDefault(req);
		} else if (type == Command.GET) {
			this.get(req.getKey(), req.getClientId());
		} else if (type == Command.REMOVE) {
			dt.remove(req.getKey());
			produceRepDefault(req);
		} else if (type == Command.RESET) {
			dt.reset();
			produceRepDefault(req);
		} else if (type == Command.CLOSE) {
			dt.reset();
			produceRepDefault(req);
			dt = null;
		}
	}
	
	private void produceRepDefault(DbRequest req) {
		DbResponse rep = new DbResponse();
		rep.setClientId(req.getClientId());
		rep.setKey(req.getKey());
		rep.setKeyType(req.getKeyType());
		rep.setValue(req.getValue());
		rep.setValueType(req.getValueType());
		db.getResponseQueue().produce(rep);
	}
	
	private void get(String key, long clientId) {
		KVObject v = dt.get(key);
		produceResponse(key, clientId, v);
	}

	private void produceResponse(String key, long cid, KVObject value) {
		DbResponse rep = new DbResponse();
		if (value == null) {
			rep.setClientId(cid);
			rep.setKey(key);
		} else {
			rep.setClientId(cid);
			rep.setKey(key);
			rep.setValue(value);
		}
		db.getResponseQueue().produce(rep);
	}

	@Override
	public boolean hasNext(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		if (index >= dt.size()) {
			return false;
		} else {
			return true;
		}
	}
	
	public KVNode<String, KVObject> next(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		if (index >= dt.size()) {
			return null;
		}
		Node<String, KVObject> e = dt.getIndex(index);
		
		KVNode<String, KVObject> n = null;
		if (e != null) {
			n = new KVNode<String, KVObject>(e.getHash(), e.getKey(), e.getValue(), e.getNext(), e.getCid());
		}
		return n == null ? none : n;
	}

	@Override
	public void expire(String key) {
		dt.remove(key);
	}
	
}
