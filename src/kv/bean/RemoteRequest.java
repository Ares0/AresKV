package kv.bean;

import com.alibaba.fastjson.annotation.JSONField;

import kv.Command;
import kv.utils.KVObject;
import kv.utils.Utils;

// remote
public class RemoteRequest {

	private String k;
	
	private KVObject v;
	
	private Command c;
	
	private boolean w;
	
	private long e;
	
	private long ci;
	
	public RemoteRequest() {}
	
	public RemoteRequest(Command command, String key, KVObject value, long clientId) {
		Utils.volidateCommond(command);
		
		this.c = command;
		this.k = key;
		this.v = value;
		this.ci = clientId;
	}
	
	public String getK() {
		return k;
	}

	public void setK(String key) {
		this.k = key;
	}

	public KVObject getV() {
		return v;
	}

	public void setV(KVObject value) {
		this.v = value;
	}

	@JSONField(serialize = false)
	public Command getC() {
		return c;
	}

	@JSONField(deserialize = false)
	public void setC(Command command) {
		this.c = command;
	}

	@JSONField(name = "c")
	public int getCCode() {
		return c.getVal();
	}

	@JSONField(name = "c")
	public void setCCode(int cc) {
		this.c = Command.getCommand(cc);
	}

	public boolean isW() {
		return w;
	}

	public void setW(boolean watch) {
		this.w = watch;
	}

	public long getE() {
		return e;
	}

	public void setE(long expireTime) {
		this.e = expireTime;
	}

	public long getCi() {
		return ci;
	}

	public void setCi(long clientId) {
		this.ci = clientId;
	}
	
}
