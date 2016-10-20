package kv.db;

import kv.db.KVMap.Node;
import kv.persistence.Dumper;

/**
 *  数据库类
 * */
public class KVDataBase {

	private static KVDataBase db;
	
	private KVMap<String, String>[] dt;
	
	private int rehashIndex;
	
	private Dumper dump;
	
	private KVDataBase() {
		this(KVMap.DEFAULT_INITIAL_CAPACITY);
	}
	
	@SuppressWarnings("unchecked")
	private KVDataBase(int initCapacity) {
		rehashIndex = -1;
		dt = new KVMap[2];
		dt[0] = new KVMap<>(initCapacity);
		
		Dumper dump = new Dumper(this);
		dump.start();
	}
	
	public static KVDataBase getDatabase() {
		return getDatabase(KVMap.DEFAULT_INITIAL_CAPACITY);
	}
	
	public static KVDataBase getDatabase(int initCapacity) {
		if (db != null) {
			return db;
		}
		synchronized (KVDataBase.class) {
			if (db == null) {
				db = new KVDataBase(initCapacity);
			}
		}
		return db;
	}
	
	// Connection
	public KVConnection getConnection() {
		KVConnection con = new KVConnection(this);
		return con;
	}

	// put rehash
	public void put(String key, String value) {
		if (rehashIndex == -1) {
			if (dt[0].resize()) {
				rehashIndex = 0;
				resize();
			}
		}
		
		if (rehashIndex != -1) {
			dt[1].put(key, value);
			rehash();
		} else {
			dt[0].put(key, value);
		}
		
	}

	private void resize() {
		int capacity = dt[0].capacity() << 1;
		if (capacity >= Integer.MAX_VALUE) {
			throw new ArrayIndexOutOfBoundsException();
		}
		capacity = capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : capacity;
		dt[1] = new KVMap<>(capacity);
	}

	// get
	public String get(String key) {
		if (rehashIndex != -1) {
			rehash();
			
			String value;
			if ((value = dt[0].get(key)) != null) {
				return value;
			} else {
				return dt[1].get(key);
			}
		} else {
			return dt[0].get(key);
		}
	}
	
	// remove
	public String remove(String key) {
		if (rehashIndex != -1) {
			dt[0].remove(key);
			dt[1].remove(key);
			rehash();
		} else {
			dt[0].remove(key);
		}
		return null;
	}
	
	private void rehash() {
		if (rehashIndex < dt[0].capacity()) {
			Node<String, String> e = dt[0].getIndex(rehashIndex);
			if (e != null) {
				dt[1].putNode(e);
			}
			rehashIndex++;
		} else {
			dt[0] = dt[1];
			dt[1] = null; // 重置
			rehashIndex = -1;
		}
	}

	// reset
	public void reset() {
		dt[0] = null;
		dt[1] = null;
		dt = null;
	}
	
	// dt[0]
	public Node<String, String>[] getNodes() {
		// 返回拷贝，会增大内存
		// 也可以直接返回，但不符合单一职责
		return dt[0].getNodes();
	}

	// close
	public void close() {
		dump.stop();
	}
	
}
