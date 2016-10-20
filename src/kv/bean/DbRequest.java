package kv.bean;

import kv.utils.KVObject;

/**
 *  command
 * */
public class DbRequest extends AbstractRequest {
	
	private static final long serialVersionUID = 1L;

	private boolean dirty;
	
	private long currentTime;
	
	public DbRequest(int command, String key, KVObject value, long id) {
		volidateCommond(command);
		
		this.command = command;
		this.key = key;
		this.value = value;
		this.clientId = id;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	
}
