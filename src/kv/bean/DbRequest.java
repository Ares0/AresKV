package kv.bean;

import kv.Command;
import kv.utils.KVObject;
import kv.utils.Utils;

/**
 *  db req
 * */
public class DbRequest {

	private String key;
	
	private KVObject value;
	
	private Command command;
	
	private boolean watch;
	
	private long expireTime;
	
	private long clientId;
	
	private boolean dirty;
	
	private long currentTime;
	
	public DbRequest(Command command, String key, KVObject value, long id) {
		Utils.volidateCommond(command);
		
		this.command = command;
		this.key = key;
		this.value = value;
		this.clientId = id;
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

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public boolean isWatch() {
		return watch;
	}

	public void setWatch(boolean watch) {
		this.watch = watch;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
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
