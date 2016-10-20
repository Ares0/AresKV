package kv.db;


/**
 *  command
 * */
public class Request<K, V> {

	public static int PUT = 1;
	
	public static int GET = 2;
	
	public static int REMOVE = 3;
	
	public static int RESET = 4;
	
	public static int CLOSE = 5;
	
	private int type;
	
	private K key;
	
	private V value;
	
	private long clientId;
	
	public Request(int type, K key, V value, long id) {
		if (type != PUT && type != GET && type != REMOVE && type != RESET && type != CLOSE) {
			throw new IllegalArgumentException();
		}
		this.type = type;
		this.key = key;
		this.value = value;
		this.clientId = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
	
}
