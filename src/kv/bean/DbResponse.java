package kv.bean;

import kv.utils.KVObject;

/**
 *  rep
 * */
public class DbResponse {

	protected String key;
	
	protected KVObject value;
	
	protected long clientId;
	
	protected boolean dirty;
	
	protected boolean move;

	public boolean isMove() {
		return move;
	}

	public void setMove(boolean move) {
		this.move = move;
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
	
}
