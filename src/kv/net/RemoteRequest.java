package kv.net;

import java.io.Serializable;

import kv.AbstractRequest;
import kv.Command;

// remote
public class RemoteRequest<K, V> extends AbstractRequest<K, V> implements Serializable{

	private static final long serialVersionUID = 3869708429918179386L;

	private int command;
	
	private K key;
	
	private V value;
	
	private boolean watch;
	
	private long expireTime;
	
	// 客户端唯一标识
	private String clientId;
	
	public RemoteRequest(int command, int keyType, int valueType, K key, V value, String clientId) {
		if (command != Command.PUT && command != Command.GET && command != Command.REMOVE && command != Command.RESET && command != Command.CLOSE) {
			throw new IllegalArgumentException();
		}
		this.command = command;
		this.key = key;
		this.value = value;
		this.clientId = clientId;
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
}
