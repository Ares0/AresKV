package kv.db;

import kv.AbstractRequest;
import kv.Command;

/**
 *  command
 * */
public class DbRequest<K, V> extends AbstractRequest<K, V> {
	
	private int command;
	
	private K key;
	
	private V value;
	
	private long clientId;
	
	private boolean watch;
	
	private boolean dirty;
	
	private long currentTime;
	
	private long expireTime;
	
	public DbRequest(int command, int keyType, int valueType, K key, V value, long id) {
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

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
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
