package kv.db.handler;

import kv.Command;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.db.util.DataTable;
import kv.db.util.KVMap.Node;
import kv.db.util.NodeFacade;

/**
 *  DataHandler
 * 在有效期、事务处理完，进行正式数据的操作。
 * */ 
public class DataHandler<K, V> extends AbstractHandler<K, V> implements Handler<K, V> {

	private DataTable<K, V> dt;
	
	private final NodeFacade<K, V> none = new NodeFacade<K, V>(0, (K)null, (V)null, null, 0);
	
	public DataHandler() {
		dt = new DataTable<>();
	}
	
	public void process(DbRequest<K, V> req) {
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
	
	private void get(K key, long clientId) {
		V v = dt.get(key);
		produceResponse(key, clientId, v);
	}

	private void produceResponse(K key, long cid, V value) {
		DbResponse<K, V> rep = new DbResponse<>();
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
	
	public NodeFacade<K, V> next(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		if (index >= dt.size()) {
			return null;
		}
		Node<K, V> e = dt.getIndex(index);
		
		NodeFacade<K, V> n = null;
		if (e != null) {
			n = new NodeFacade<K, V>(e.getHash(), e.getKey(), e.getValue(), e.getNext(), e.getCid());
		}
		return n == null ? none : n;
	}

	@Override
	public void expire(K key) {
		dt.remove(key);
	}
	
}
