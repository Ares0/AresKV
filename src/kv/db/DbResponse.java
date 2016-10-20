package kv.db;

import kv.AbstractResponse;
import kv.utils.KVObject;

/**
 * Response
 * */
public class DbResponse extends AbstractResponse {

	private String key;
	
	private KVObject value;
	
	private long clientId;
	
	private boolean dirty;

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

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean watch) {
		this.dirty = watch;
	}
	
}
