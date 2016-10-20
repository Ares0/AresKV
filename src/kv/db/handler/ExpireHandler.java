package kv.db.handler;

import kv.db.Request;
import kv.db.Response;
import kv.db.util.DataTable;
import kv.db.util.NodeFacade;
import kv.db.util.KVMap.Node;


/**
 *  ExpireHandler
 * next时判断存在&&过期，符合条件则返回。
 * */
public class ExpireHandler<K, V> extends AbstractHandler<K, V> implements Handler<K, V> {
	
	private DataTable<K, Request<K, V>> dt;
	
	public ExpireHandler() {
		dt = new DataTable<>();
	}
	
	@SuppressWarnings("unchecked")
	public void process(Request<K, V> req) {
		K key = req.getKey();
		int type = req.getType();
		long current = System.currentTimeMillis();
		
		if (type == Request.PUT && req.getExpireTime() != 0) {
			req.setCurrentTime(current);
			dt.put(key, req);
		} else if (type == Request.GET) {
			Request<K, V> reqExp = dt.get(key);
			if (reqExp != null && isExpire(reqExp, current)) {
				Response<K, V> rep = expireReponse(req);
				
				db.getResponseQueue().produce((Response<String, String>) rep);
				next.expire(key);
				// 过期返回
				return;
			}
		} else if (type == Request.REMOVE) {
			dt.remove(key);
		} else if (type == Request.RESET) {
			dt.reset();
		} else if (type == Request.CLOSE) {
			dt.reset();
			dt = null;
		} else if (type == Request.PUT) {
			;
		} else {
			throw new IllegalArgumentException();
		}
		
		// 继续处理链
		next.process(req);
	}
	
	private boolean isExpire(Request<K, V> req, long current) {
		long old = req.getCurrentTime();
		long expire = req.getExpireTime();
		
		if (old + expire < current) {
			return true;
		}
		return false;
	}

	public NodeFacade<K, V> next(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		if (index >= dt.size()) {
			return next.next(index);
		}
		
		long current = System.currentTimeMillis();
		Node<K, Request<K, V>> e = dt.getIndex(index);
		Request<K, V> req = e == null ? null : e.getValue();
		
		if (req != null && !isExpire(req, current)) {
			return next.next(index);
		} else {
			Response<K, V> rep = expireReponse(req);
			return new NodeFacade<K, V>(0, rep.getKey(), null, null);
		}
	}
	
	private Response<K, V> expireReponse(Request<K, V> req) {
		K key = req.getKey();
		
		Response<K, V> rep = new Response<>();
		rep.setClientId(req.getClientId());
		rep.setKey(key);
		
		dt.remove(key);
		return rep;
	}

}
