package kv.net;

import java.util.Map;
import java.util.WeakHashMap;

import kv.Command;
import kv.KVDataBase;
import kv.db.DbRequest;
import kv.db.DbResponse;
import kv.synchro.SpinSynchronous;
import kv.synchro.Synchronous;
import kv.utils.KVObject;

/**
 *  Connection
 * 如果不同的客户端不断连接过来，Map会很大且难以回收；
 * 可能需要做会话管理。
 * */
public class KVConnection {
	
	private final KVDataBase db;
	
	private Map<String, Long> reps;
	
	private Synchronous syn;
	
	private int spinCount;
	
	public KVConnection(KVDataBase db) {
		this(db, new SpinSynchronous());
	}
	
	public KVConnection(KVDataBase db, Synchronous syn) {
		this.db = db;
		this.syn = syn;
		// 帮助回收不用的client
		this.reps = new WeakHashMap<>();
	}

	public RemoteResponse process(RemoteRequest req) {
		int com = req.getCommand();
		String ke = req.getKey();
		KVObject val = req.getValue();
		long ex = req.getExpireTime();
		boolean wa = req.isWatch();
		String cid = req.getClientId();
		
		RemoteResponse rep = new RemoteResponse();
		
		if (com == Command.PUT) {
			if (ex == 0 && !wa) {
				this.put(ke, val, cid, req);
			} else if (ex != 0 && !wa) {
				this.put(ke, val, wa, cid, req);
			} else if (ex != 0 && wa) {
				this.put(ke, val, ex, wa, cid, req);
			} else {
				System.out.println("connection wrong argument" + ke + cid);
			}
			rep.setKey(ke);
		} else if (com == Command.GET) {
			KVObject a = this.get(ke, cid, req);
			rep.setKey(ke);
			rep.setValue(a);
		} else if (com == Command.REMOVE) {
			this.remove(ke, cid, req);
			rep.setKey(ke);
		} else if (com == Command.RESET) {
			this.reset(cid, req);
			rep.setKey(ke);
		} else if (com == Command.CLOSE) {
			this.close(cid, req);
			rep.setKey(ke);
		} else {
			System.out.println("connection wrong argument" + ke + cid);
		}
		return rep;
	}
	
	public void put(String ke, KVObject val, String cid, RemoteRequest req) {
		db.getRequestQueue().produce(new DbRequest(Command.PUT, req.getKeytype(), req.getValuetype(), ke, val, getClientId(cid)));
	}
	
	public void put(String ke, KVObject val, long expire, String cid, RemoteRequest req) {
		DbRequest r = new DbRequest(Command.PUT,req.getKeytype(), req.getValuetype(),  ke, val, getClientId(cid));
		r.setExpireTime(expire);
		db.getRequestQueue().produce(r);
	}
	
	public void put(String key, KVObject value, boolean watch, String cid, RemoteRequest req) {
		DbRequest r = new DbRequest(Command.PUT, req.getKeytype(), req.getValuetype(), key, value, getClientId(cid));
		r.setWatch(watch);
		db.getRequestQueue().produce(r);
	}
	
	public void put(String ke, KVObject val, long expire, boolean watch, String cid, RemoteRequest req) {
		DbRequest r = new DbRequest(Command.PUT, req.getKeytype(), req.getValuetype(), ke, val, getClientId(cid));
		r.setExpireTime(expire);
		r.setWatch(watch);
		db.getRequestQueue().produce(r);
	}
	
	//  一直到有值产生
	// 保存Thread对用的clientId
	public KVObject get(String ke, String cid, RemoteRequest req) {
		long clientId = getClientId(cid);
		db.getRequestQueue().produce(new DbRequest(Command.GET, req.getKeytype(), req.getValuetype(), ke, null, clientId));
		
		DbResponse value;
		while ((value = db.getResponseQueue().consume(clientId)) == null) {
			syn.doSynchronous();
			spinCount++;
		}
		
		return value.getValue();
	}
	
	public void remove(String ke, String cid, RemoteRequest req) {
		db.getRequestQueue().produce(new DbRequest(Command.GET, req.getKeytype(), req.getValuetype(), ke, null, getClientId(cid)));
	}
	
	public void reset(String cid, RemoteRequest req) {
		db.getRequestQueue().produce(new DbRequest(Command.RESET, req.getKeytype(), req.getValuetype(), null, null, getClientId(cid)));
	}
	
	public void close(String cid, RemoteRequest req) {
		System.out.println("connection spin " + spinCount);
		db.getRequestQueue().produce(new DbRequest(Command.CLOSE, req.getKeytype(), req.getValuetype(), null, null, getClientId(cid)));
	}
	
	// cid sid
	private long getClientId(String cid) {
		Long clientId ;
		if ((clientId = reps.get(cid)) == null) {
			reps.put(cid, (clientId = db.getClientId()));
		}
		return clientId;
	}
	
}
