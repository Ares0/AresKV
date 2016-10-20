package kv.net;

import java.io.Serializable;

import kv.AbstractResponse;

// remote
public class RemoteResponse<K, V> extends AbstractResponse<K, V> implements Serializable{

	private static final long serialVersionUID = -3917534195643220770L;

	private K key;
	
	private V value;
	
	private boolean dirty;

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

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean watch) {
		this.dirty = watch;
	}
	
}
