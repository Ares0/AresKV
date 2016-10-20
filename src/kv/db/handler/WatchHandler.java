package kv.db.handler;

import kv.Command;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.utils.DataTable;


/**
 *  WatchHandler
 * Get时如果发现有，就返回失败。
 * */ 
public class WatchHandler extends AbstractHandler implements Handler {
	
	private DataTable<String, DbRequest> dt;
	
	public WatchHandler() {
		dt = new DataTable<>();
	}
	
	public void process(DbRequest req) {
		String key = req.getKey();
		int type = req.getCommand();
		DbRequest reqWatch = dt.get(key);
		long cid = req.getClientId();
		
		if (type == Command.PUT || type == Command.REMOVE) {
			if (reqWatch == null && req.isWatch()) {
				dt.put(key, req, cid);
			} else if (isDirty(reqWatch, cid)) {
				doDirtyRep(req, key, reqWatch);
				return;  // 脏数据返回
			} else if (reqWatch != null) {
				req.setDirty(true);
				dt.put(key, req, cid); 
			}
		} else if (type == Command.GET) {
			if (isDirty(reqWatch, cid)) {
				doDirtyRep(req, key, reqWatch);
				return;  // 脏数据返回
			}
		} else if (type == Command.RESET) {
			dt.reset();
		} else if (type == Command.CLOSE) {
			dt.reset();
			dt = null;
		} else {
			throw new IllegalArgumentException();
		}
		
		// 继续处理链
		next.process(req);
	}

	private void doDirtyRep(DbRequest req, String key, DbRequest reqWatch) {
		DbResponse rep = new DbResponse();
		rep.setClientId(req.getClientId());
		rep.setKey(reqWatch.getKey());
		rep.setValue(reqWatch.getValue());
		rep.setDirty(true);
		
		db.getResponseQueue().produce(rep);
		dt.remove(key);
	}
	
	private boolean isDirty(DbRequest rw, long cid) {
		return (rw != null && rw.isDirty() && cid == rw.getClientId());
	}
	
	@Override
	public void expire(String key) {
		dt.remove(key);
		next.expire(key);
	}

}
