package kv.db;

import kv.AbstractRequest;
import kv.Command;
import kv.utils.KVObject;

/**
 *  command
 * */
public class DbRequest extends AbstractRequest {
	
	private int command;
	
	private String key;
	
	private KVObject value;
	
	private long clientId;
	
	private boolean watch;
	
	private boolean dirty;
	
	private long currentTime;
	
	private long expireTime;
	
	public DbRequest(int command, int keyType, int valueType, String key, KVObject value, long id) {
		if (command != Command.PUT && command != Command.GET 
				&& command != Command.REMOVE && command != Command.RESET && command != Command.CLOSE) {
			throw new IllegalArgumentException();
		}
		this.command = command;
		this.key = key;
		this.value = value;
		this.clientId = id;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
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

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long l) {
		this.clientId = l;
	}

	public boolean isWatch() {
		return watch;
	}

	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}
	
}
