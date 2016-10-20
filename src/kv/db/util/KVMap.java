package kv.db.util;

import java.util.Objects;


/**
 *  Map µœ÷
 * */
public class KVMap<K, V> {

	private Node<K,V>[] table;
	
	public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
	
	private static final int MAXIMUM_CAPACITY = Integer.MAX_VALUE;
	
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private int size;
	
	private int threshold;
	
	private float loadFactor;
	
	private int initalCapacity;
	
	public KVMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}
	
	public KVMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public KVMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
		
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
		this.loadFactor = loadFactor;
		this.initalCapacity = initialCapacity;
	}
	
	public int size() {
        return size;
    }
	
	public int capacity() {
		return initalCapacity;
	}
	
	public boolean isReSize() {
		if (size == 0) {
			return false;
		} else {
			return size >= threshold;
		}
    }
	
	private static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
	
	public V get(Object key) {
        Node<K, V> e = getNode(hash(key), key);
        return e == null ? null : e.value;
    }
	
	public Node<K, V> getIndex(int index) {
		if (index >= size) 
			return null;
        return table[index];
    }
	
	private final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; 
        Node<K,V> first, e; 
        int n; K k;
        
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && 
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
	
    public V put(K key, V value) {
        return putVal(hash(key), key, value);
    }
	
	private final V putVal(int hash, K key, V value) {
		 Node<K,V>[] tab; 
		 Node<K,V> p; 
		 int n, i;
		 
		 if ((tab = table) == null || (n = tab.length) == 0)
		     n = (tab = init()).length;
		 if ((p = tab[i = (n - 1) & hash]) == null)
		     tab[i] = newNode(hash, key, value, null);
		 else {
		     Node<K,V> e; K k;
		     if (p.hash == hash &&
		         ((k = p.key) == key || (key != null && key.equals(k))))
		         e = p;		    
		     else {
		         for (;;) {
		             if ((e = p.next) == null) {
		                 p.next = newNode(hash, key, value, null);
		                 break;
		             }
		             if (e.hash == hash &&
		                 ((k = e.key) == key || (key != null && key.equals(k))))
		                 break;
		             p = e;
		         }
		     }
		     if (e != null) { 
		    	 V oldValue = e.value;
		    	 e.value = value;
		         return oldValue;
		     }
		 }
		 ++size;
		 return null;
	}
	
	@SuppressWarnings("unchecked")
	private Node<K, V>[] init() {
    	table = new Node[initalCapacity];
    	threshold = (int)(table.length * loadFactor);
		return table;
	}

	private Node<K,V> newNode(int hash, K key, V value, Node<K,V> next) {
        return new Node<>(hash, key, value, next);
    }
	
	public V remove(Object key) {
		Node<K,V> e = removeNode(hash(key), key);
        return e == null ? null : e.value;
	}
	
	private Node<K, V> removeNode(int hash, Object key) {
    	Node<K,V>[] tab; 
        Node<K,V> first = null, e = null; 
        Node<K,V> node = null, pre = null; 
        int n;
        K k;
        int index = 0;
        
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[index = (n - 1) & hash]) != null) {
            if (first.hash == hash && 
                ((k = first.key) == key || (key != null && key.equals(k))))
            	node = first;
            else if ((e = first.next) != null) {
            	pre = first;
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                    	node = e;
                    	break;
                    }
                    pre = e;
                } while ((e = e.next) != null);
            }
            
            if (node != null) {
            	if (node == first) {
            		tab[index] = node.next;
            	} else {
            		pre.next = node.next;
            	}
            	--size;
            }
        }
        return null;
	}

	public void putNode(Node<K, V> e) {
		 do {
			 this.put(e.key, e.value);
		 } while ((e = e.next) != null);
	}
	
	// node
	public static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final int getHash()     {return hash; }
        public Node<K, V> getNext()    { return next; }
        
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
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

}
