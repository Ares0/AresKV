package kv.db.handler;

import kv.Command;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.utils.DataTable;
import kv.utils.NodeFacade;
import kv.utils.KVMap.Node;
import kv.utils.KVObject;

/**
 *  DataHandler
 * 在有效期、事务处理完，进行正式数据的操作。
 * */ 
public class DataHandler extends AbstractHandler implements Handler {

	private DataTable<String, KVObject> dt;
	
	private final NodeFacade<String, KVObject> none = new NodeFacade<>(0, (String)null, (KVObject)null, null, 0);
	
	public DataHandler() {
		dt = new DataTable<>();
	}
	
	public void process(DbRequest req) {
		int type = req.getCommand();
		
		if (type == Command.PUT) {
			dt.put(req.getKey(), req.getValue(), req.getClientId());
		} else if (type == Command.GET) {
			this.get(req.getKey(), req.getClientId());
		} else if (type == Command.REMOVE) {
			dt.remove(req.getKey());
		} else if (type == Command.RESET) {
			dt.reset();
		} else if (type == Command.CLOSE) {
			dt.reset();
			dt = null;
		} else {
			throw new IllegalArgumentException();
		}
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
	
	public NodeFacade<String, KVObject> next(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		if (index >= dt.size()) {
			return null;
		}
		Node<String, KVObject> e = dt.getIndex(index);
		
		NodeFacade<String, KVObject> n = null;
		if (e != null) {
			n = new NodeFacade<String, KVObject>(e.getHash(), e.getKey(), e.getValue(), e.getNext(), e.getCid());
		}
		return n == null ? none : n;
	}

	@Override
	public void expire(String key) {
		dt.remove(key);
	}
	
}
