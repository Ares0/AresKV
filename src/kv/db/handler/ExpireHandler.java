package kv.db.handler;

import kv.Command;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.utils.DataTable;
import kv.utils.NodeFacade;
import kv.utils.KVMap.Node;
import kv.utils.KVObject;


/**
 *  ExpireHandler
 * next时判断存在&&过期，符合条件则返回。
 * */
public class ExpireHandler extends AbstractHandler implements Handler {
	
	private DataTable<String, DbRequest> dt;
	
	private final NodeFacade<String, KVObject> none = new NodeFacade<>(0, null, null, null, 0);
	
	public ExpireHandler() {
		dt = new DataTable<>();
	}
	
	public void process(DbRequest req) {
		String key = req.getKey();
		int type = req.getCommand();
		long current = System.currentTimeMillis();
		
		if (type == Command.PUT && req.getExpireTime() != 0) {
			req.setCurrentTime(current);
			dt.put(key, req, req.getClientId());
		} else if (type == Command.GET) {
			DbRequest reqExp = dt.get(key);
			if (reqExp != null && isExpire(reqExp, current)) {
				DbResponse rep = expireReponse(req);
				
				db.getResponseQueue().produce(rep);
				next.expire(key);
				// 过期返回
				return;
			}
		} else if (type == Command.REMOVE) {
			dt.remove(key);
		} else if (type == Command.RESET) {
			dt.reset();
		} else if (type == Command.CLOSE) {
			dt.reset();
			dt = null;
		} else if (type == Command.PUT) {
			;
		} else {
			throw new IllegalArgumentException();
		}
		
		// 继续处理链
		next.process(req);
	}
	
	private boolean isExpire(DbRequest req, long current) {
		long old = req.getCurrentTime();
		long expire = req.getExpireTime();
		
		if (old + expire < current) {
			return true;
		}
		return false;
	}

	public NodeFacade<String, KVObject> next(int index) {
		if (index <= 0 || index > Integer.MAX_VALUE) {
			throw new IllegalArgumentException();
		}
		
		if (index >= dt.size()) {
			return next.next(index);
		}
		
		long current = System.currentTimeMillis();
		Node<String, DbRequest> e = dt.getIndex(index);
		DbRequest req = e == null ? null : e.getValue();
		
		if (req != null && isExpire(req, current)) {
			expireNext(req);
			return none;
		} else {
			return next.next(index);
		}
	}
	
	private void expireNext(DbRequest req) {
		String key = req.getKey();
		dt.remove(key);
	}
	
	private DbResponse expireReponse(DbRequest req) {
		String key = req.getKey();
		
		DbResponse rep = new DbResponse();
		rep.setClientId(req.getClientId());
		rep.setKey(key);
		
		dt.remove(key);
		return rep;
	}

}
