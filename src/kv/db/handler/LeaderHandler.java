package kv.db.handler;

import kv.Command;
import kv.bean.DbRequest;
import kv.db.MasterSlaveDB;

/**
 *  LeaderHandler
 * */
public class LeaderHandler extends AbstractHandler {

	private static DbRequest dirtyReq;
	
	private static DbRequest expireReq;
	
	public LeaderHandler() {
		dirtyReq = new DbRequest(Command.DIRTY, null, null, 0);
		expireReq = new DbRequest(Command.EXPIRE, null, null, 0);
	}
	
	public void process(DbRequest req) {
		int type = req.getCommand();
		if (type == Command.PUT || type == Command.REMOVE) {
			sendLeaderMsg(req);
		}
		next.process(req);   // 继续处理链
	}

	public void expire(String key) {
		expireReq.setKey(key);
		sendLeaderMsg(expireReq);
	}

	public void dirty(DbRequest req) {
		dirtyReq.setKey(req.getKey());
		dirtyReq.setClientId(req.getClientId());
		dirtyReq.setDirty(true);
		sendLeaderMsg(dirtyReq);
	}
	
	// 生产消费模式
	private void sendLeaderMsg(DbRequest req) {
		((MasterSlaveDB)db).getDepRequests().produce(req);
	}

}
