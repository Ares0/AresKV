package kv.db.handler;

import kv.Command;
import kv.bean.DbRequest;
import kv.bean.DbResponse;
import kv.db.MasterSlaveDB;
import kv.db.MasterSlaveDB.DBState;
import kv.utils.DataTable;


/**
 *  WatchHandler
 * Get时如果发现有，就返回失败。
 * */ 
public class WatchHandler extends AbstractHandler {
	
	private DataTable<String, DbRequest> dt;
	
	public WatchHandler() {
		dt = new DataTable<>();
	}
	
	public void process(DbRequest req) {
		String key = req.getKey();
		Command type = req.getCommand();
		DbRequest reqWatch = dt.get(key);
		long cid = req.getClientId();
		
		if (db instanceof MasterSlaveDB) {
			if (((MasterSlaveDB) db).getState() == DBState.FOLLOWERING
					&& type == Command.DIRTY) {
				dt.put(key, req, cid);
				next.dirty(req);
			}
		}
		
		if (type == Command.PUT 
				|| type == Command.REMOVE) {
			// 进入监视
			if (reqWatch == null && req.isWatch()) {
				dt.put(key, req, cid);
			}
			// 同客户端
			else if (reqWatch != null && isDirty(reqWatch, cid)) {
				doDirtyResponse(req, key, reqWatch);
				next.dirty(req);
			}
			// 脏标识设置
			else if (reqWatch != null) {
				reqWatch.setDirty(true);
			}
		} else if (type == Command.GET) {
			if (isDirty(reqWatch, cid)) {
				doDirtyResponse(req, key, reqWatch);
				
				next.dirty(req);
				return;            // 脏数据返回
			}
		} else if (type == Command.RESET) {
			dt.reset();
		} else if (type == Command.CLOSE) {
			dt.reset();
			dt = null;
		} 
		
		next.process(req);   // 不是脏数据，继续处理链
	}

	private void doDirtyResponse(DbRequest req, String key, DbRequest reqWatch) {
		DbResponse rep = new DbResponse();
		rep.setClientId(req.getClientId());
		rep.setKey(reqWatch.getKey());
		rep.setValue(reqWatch.getValue());
		rep.setDirty(true);
		
		db.getResponseQueue().produce(rep);
		dt.remove(key);
	}
	
	private boolean isDirty(DbRequest rw, long cid) {
		return (rw.isDirty() && cid == rw.getClientId());
	}
	
	@Override
	public void expire(String key) {
		dt.remove(key);
		next.expire(key);
	}

}
