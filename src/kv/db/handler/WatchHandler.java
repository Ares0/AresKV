package kv.db.handler;

import kv.db.Request;
import kv.db.Response;
import kv.db.util.DataTable;


/**
 *  WatchHandler
 * Getʱ��������У��ͷ���ʧ�ܡ�
 * */ 
public class WatchHandler<K, V> extends AbstractHandler<K, V> implements Handler<K, V> {
	
	private DataTable<K, Request<K, V>> dt;
	
	public WatchHandler() {
		dt = new DataTable<>();
	}
	
	@SuppressWarnings("unchecked")
	public void process(Request<K, V> req) {
		K key = req.getKey();
		int type = req.getType();
		Request<K, V> reqWatch = dt.get(key);
		
		if (type == Request.PUT || type == Request.REMOVE) {
			if (reqWatch == null && req.isWatch()) {
				dt.put(key, req);
			}
			if (reqWatch != null) {
				reqWatch.setDirty(true);
				dt.put(key, reqWatch); 
			}
		} else if (type == Request.GET) {
			if (reqWatch != null && reqWatch.isDirty() 
					&& req.getClientId() == reqWatch.getClientId()) {
				Response<K, V> rep = new Response<>();
				rep.setClientId(req.getClientId());
				rep.setKey(reqWatch.getKey());
				rep.setValue(reqWatch.getValue());
				rep.setDirty(true);
				
				db.getResponseQueue().produce((Response<String, String>) rep);
				dt.remove(key);
				// �����ݷ���
				return;
			}
		} else if (type == Request.RESET) {
			dt.reset();
		} else if (type == Request.CLOSE) {
			dt.reset();
			dt = null;
		} else {
			throw new IllegalArgumentException();
		}
		
		// ����������
		next.process(req);
	}

	@Override
	public void expire(K key) {
		dt.remove(key);
		next.expire(key);
	}

}
