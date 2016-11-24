package kv.db;

import kv.Command;
import kv.KVDataBase;
import kv.bean.DbRequest;
import kv.bean.DbResponse;
import kv.bean.RemoteRequest;
import kv.bean.RemoteResponse;
import kv.synchro.Synchronous;
import kv.synchro.SynchronousFactory;
import kv.utils.KVObject;
import kv.utils.Utils;

/**
 *   Adapter
 * 客户端保存clientId、服务器只生成顺序id。
 * */
public class KVConnection {
	
	private final KVDataBase db;
	
	private Synchronous syn;
	
	private int spinCount;
	
	public KVConnection(KVDataBase db) {
		this(db, SynchronousFactory.getSpinSynchronous());
	}
	
	public KVConnection(KVDataBase db, Synchronous syn) {
		this.db = db;
		this.syn = syn;
	}

	public RemoteResponse process(RemoteRequest req) {
		Command com = req.getC();
		String ke = req.getK();
		KVObject val = req.getV();
		
		long ex = req.getE();
		boolean wa = req.isW();
		long cid = req.getCi();
		
		RemoteResponse rep = null;
		
		if (com == Command.PUT) {
			if (ex == 0 && !wa) {
				rep = put(ke, val, cid, req);
			} else if (ex != 0 && !wa) {
				rep = put(ke, val, wa, cid, req);
			} else if (ex != 0 && wa) {
				rep = put(ke, val, ex, wa, cid, req);
			} else {
				System.out.println("connection wrong argument" + ke + cid);
			}
		} else if (com == Command.GET) {
			rep = get(ke, cid, req);
		} else if (com == Command.REMOVE) {
			rep = remove(ke, cid, req);
		} else if (com == Command.RESET) {
			rep = reset(ke, cid, req);
		} else if (com == Command.CLOSE) {
			rep = close(ke, cid, req);
		} else {
			System.out.println("connection wrong argument" + ke + cid);
		}
		return rep;
	}
	
	public RemoteResponse put(String ke, KVObject val, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.PUT, ke, val, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId, ke);
		return rep;
	}
	
	public RemoteResponse put(String ke, KVObject value, boolean watch, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		DbRequest r = new DbRequest(Command.PUT, ke, value, clientId);
		
		r.setWatch(watch);
		db.getRequestQueue().produce(r);
		
		RemoteResponse rep = prepareRemoteRep(clientId, ke);
		return rep;
	}
	
	public RemoteResponse put(String ke, KVObject val, long expire, boolean watch, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		DbRequest r = new DbRequest(Command.PUT, ke, val, clientId);
		
		r.setExpireTime(expire);
		r.setWatch(watch);
		db.getRequestQueue().produce(r);
		
		RemoteResponse rep = prepareRemoteRep(clientId, ke);
		return rep;
	}
	
	public RemoteResponse get(String ke, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.GET, ke, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId, ke);
		return rep;
	}
	
	public RemoteResponse remove(String ke, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.GET,	ke, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId, ke);
		return rep;
	}
	
	public RemoteResponse reset(String ke, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.RESET, null, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId, ke);
		return rep;
	}
	
	public RemoteResponse close(String ke, long cid, RemoteRequest req) {
		System.out.println("connection spin " + spinCount);
		
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.CLOSE, null, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId, ke);
		return rep;
	}
	
	// cid sid
	private long getClientId(long cid) {
		if (cid == 0 || cid <= 0) {
			cid = db.getClientId();
		}
		return cid;
	}

	private RemoteResponse prepareRemoteRep(long cid, String key) {
		DbResponse dp = getDbResponse(cid, key);
		RemoteResponse rep = new RemoteResponse();
		
		rep.setCi(dp.getClientId());
		rep.setD(dp.isDirty());
		rep.setK(dp.getKey());
		rep.setM(dp.isMove());
		rep.setV(dp.getValue());
		
		dp = null;  // gc db rep
		return rep;
	}
	
	// cusume repConcurrentQueue
	private DbResponse getDbResponse(long cid, String ke) {
		DbResponse dp;
		String key = Utils.getCidKey(cid, ke);
		
		while ((dp = db.getResponseQueue().consume(key)) == null) {
			syn.doSynchronous();
			spinCount++;
		}
		return dp;
	}
	
}
