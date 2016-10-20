package kv.db.util;

import kv.db.util.KVMap.Node;

public class DataTable<K, V> {

	private KVMap<K, V>[] dt;
	
	private int rehashIndex;
	
	private boolean isRehash;
	
	@SuppressWarnings("unchecked")
	public DataTable() {
		this.isRehash = false;
		this.rehashIndex = -1;
		
		dt = new KVMap[2];
		dt[0] = new KVMap<>();
	}
	
	public int size() {
		return dt[0].capacity();
	}
	
	public V get(Object key) {
        V e;
		if (isRehash) {
			if ((e = dt[1].get(key)) == null) {
				e = dt[0].get(key);
			} 
			rehash();
		} else {
			e = dt[0].get(key);
		}
        return e == null ? null : e;
    }
	
	public Node<K, V> getIndex(int index) {
		Node<K, V> e = dt[0].getIndex(index);
        return e;
    }
	
    public V put(K key, V value) {
    	if (!isRehash) {
			if (dt[0].isReSize()) {
				isRehash = true;
				rehashIndex = 0;
				resize();
			}
		}
		
    	V v;
		if (isRehash) {
			v = dt[1].put(key, value);
			rehash();
		} else {
			v = dt[0].put(key, value);
		}
        return v;
    }

	private void resize() {
		int capacity = dt[0].capacity() << 1;
		if (capacity >= Integer.MAX_VALUE) {
			throw new ArrayIndexOutOfBoundsException();
		}
		capacity = capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : capacity;
		dt[1] = new KVMap<>(capacity);
	}
	
	private void rehash() {
		if (rehashIndex < dt[0].capacity()) {
			Node<K, V> e = dt[0].getIndex(rehashIndex);
			if (e != null) {
				dt[1].putNode(e);
			}
			rehashIndex++;
		} else {
			dt[0] = dt[1];
			dt[1] = null;
			
			isRehash = false;
			rehashIndex = -1;
		}
	}
	
	public V remove(Object key) {
		V e;
		if (isRehash) {
			dt[0].remove(key);
			e = dt[1].remove(key);
			rehash();
		} else {
			e = dt[0].remove(key);
		}
        return e == null ? null : e;
	}
	
	public void reset() {
		dt[0] = null;
		dt[1] = null;
		dt = null;
	}
	
}
