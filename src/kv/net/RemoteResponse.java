package kv.net;

import java.io.Serializable;

import kv.AbstractResponse;
import kv.utils.KVObject;

// remote
public class RemoteResponse extends AbstractResponse implements Serializable{

	private static final long serialVersionUID = -3917534195643220770L;

	private String key;
	
	private KVObject value;
	
	private boolean dirty;
	
	// 返回唯一标识
	private long clientId;

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public KVObject getValue() {
		return value;
	}

	public void setValue(KVObject value) {
		this.value = value;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean watch) {
		this.dirty = watch;
	}
	
}
