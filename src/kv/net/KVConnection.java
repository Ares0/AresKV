package kv.net;

import kv.Command;
import kv.KVDataBase;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.synchro.SpinSynchronous;
import kv.synchro.Synchronous;
import kv.utils.KVObject;

/**
 *   Adapter
 *  不同的客户端不断连接过来，Map会很大且难以回收；
 * 客户端保存clientId、服务器只生成顺序id。
 * */
public class KVConnection {
	
	private final KVDataBase db;
	
	private Synchronous syn;
	
	private int spinCount;
	
	public KVConnection(KVDataBase db) {
		this(db, new SpinSynchronous());
	}
	
	public KVConnection(KVDataBase db, Synchronous syn) {
		this.db = db;
		this.syn = syn;
	}

	public RemoteResponse process(RemoteRequest req) {
		int com = req.getCommand();
		String ke = req.getKey();
		KVObject val = req.getValue();
		
		long ex = req.getExpireTime();
		boolean wa = req.isWatch();
		long cid = req.getClientId();
		
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
			rep = reset(cid, req);
		} else if (com == Command.CLOSE) {
			rep = close(cid, req);
		} else {
			System.out.println("connection wrong argument" + ke + cid);
		}
		return rep;
	}
	
	public RemoteResponse put(String ke, KVObject val, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.PUT,
				req.getKeyType(), req.getValueType(), ke, val, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId);
		return rep;
	}
	
	public RemoteResponse put(String key, KVObject value, boolean watch, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		DbRequest r = new DbRequest(Command.PUT, req.getKeyType(),
				req.getValueType(), key, value, clientId);
		
		r.setWatch(watch);
		db.getRequestQueue().produce(r);
		
		RemoteResponse rep = prepareRemoteRep(clientId);
		return rep;
	}
	
	public RemoteResponse put(String ke, KVObject val, long expire, boolean watch, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		DbRequest r = new DbRequest(Command.PUT, req.getKeyType(),
				req.getValueType(), ke, val, clientId);
		
		r.setExpireTime(expire);
		r.setWatch(watch);
		db.getRequestQueue().produce(r);
		
		RemoteResponse rep = prepareRemoteRep(clientId);
		return rep;
	}
	
	public RemoteResponse get(String ke, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.GET,
				req.getKeyType(), req.getValueType(), ke, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId);
		return rep;
	}
	
	public RemoteResponse remove(String ke, long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.GET,
				req.getKeyType(), req.getValueType(), ke, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId);
		return rep;
	}
	
	public RemoteResponse reset(long cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.RESET,
				req.getKeyType(), req.getValueType(), null, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId);
		return rep;
	}
	
	public RemoteResponse close(long cid, RemoteRequest req) {
		System.out.println("connection spin " + spinCount);
		
		long clientId = getClientId(cid);
		
		db.getRequestQueue().produce(new DbRequest(Command.CLOSE,
				req.getKeyType(), req.getValueType(), null, null, clientId));
		
		RemoteResponse rep = prepareRemoteRep(clientId);
		return rep;
	}
	
	// cid sid
	private long getClientId(long cid) {
		if (cid == 0 || cid <= 0) {
			cid = db.getClientId();
		}
		return cid;
	}

	private RemoteResponse prepareRemoteRep(long clientId) {
		DbResponse dp = getDbResponse(clientId);
		RemoteResponse rep = new RemoteResponse();
		
		rep.setClientId(dp.getClientId());
		rep.setDirty(dp.isDirty());
		rep.setKey(dp.getKey());
		rep.setKeyType(dp.getKeyType());
		rep.setMove(dp.isMove());
		rep.setValue(dp.getValue());
		rep.setValueType(dp.getValueType());
		return rep;
	}
	
	// cusume
	private DbResponse getDbResponse(long cid) {
		DbResponse dp;
		while ((dp = db.getResponseQueue().consume(cid)) == null) {
			syn.doSynchronous();
			spinCount++;
		}
		return dp;
	}
	
}
