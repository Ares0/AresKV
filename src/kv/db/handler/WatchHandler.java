package kv.db.handler;

import kv.Command;
import kv.bean.DbRequest;
import kv.bean.DbResponse;
import kv.db.MasterSlaveDB;
import kv.db.MasterSlaveDB.DBState;
import kv.utils.DataTable;


/**
 *  WatchHandler
 * Getʱ��������У��ͷ���ʧ�ܡ�
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
			// �������
			if (reqWatch == null && req.isWatch()) {
				dt.put(key, req, cid);
			}
			// ͬ�ͻ���
			else if (reqWatch != null && isDirty(reqWatch, cid)) {
				doDirtyResponse(req, key, reqWatch);
				next.dirty(req);
			}
			// ���ʶ����
			else if (reqWatch != null) {
				reqWatch.setDirty(true);
			}
		} else if (type == Command.GET) {
			if (isDirty(reqWatch, cid)) {
				doDirtyResponse(req, key, reqWatch);
				
				next.dirty(req);
				return;            // �����ݷ���
			}
		} else if (type == Command.RESET) {
			dt.reset();
		} else if (type == Command.CLOSE) {
			dt.reset();
			dt = null;
		} 
		
		next.process(req);   // ���������ݣ�����������
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
