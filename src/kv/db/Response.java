package kv.db;


/**
 * Response
 * */
public class Response<K, V> {

	private K key;
	
	private V value;
	
	private long clientId;
	
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
