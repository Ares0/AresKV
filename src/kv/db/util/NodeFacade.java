package kv.db.util;

import java.util.Objects;

import kv.db.util.KVMap.Node;

public class NodeFacade<K, V> {

    private final int hash;
    private final K key;
    private V value;
    private Node<K, V> next;
    private long current;
    private long expire;
    private boolean iswatch;
    private boolean isDirty;

    public NodeFacade(int hash, K key, V value, Node<K, V> next) {
        this.hash = hash;
        this.key = key;
        this.value = value;
        this.next = next;
    }

    public final K getKey()        { return key; }
    public final V getValue()      { return value; }
    
    public final int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    public Node<K, V> getNext() {
		return next;
	}

	public void setNext(Node<K, V> next) {
		this.next = next;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public boolean isIswatch() {
		return iswatch;
	}

	public void setIswatch(boolean iswatch) {
		this.iswatch = iswatch;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public int getHash() {
		return hash;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
    public final boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Node) {
			Node<K, V> e = (Node<K, V>)o;
            if (Objects.equals(key, e.getKey()) &&
                Objects.equals(value, e.getValue()))
                return true;
        }
        return false;
    }
	
}
