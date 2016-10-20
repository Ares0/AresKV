package kv.db;


/**
 *  Èë¿ÚÀà
 * */
public class KVConnection {
	
	private final KVDataBase db;
	
	public KVConnection(KVDataBase db) {
		this.db = db;
	}

	public void put(String key, String value) {
		db.put(key, value);
	}
	
	public String get(String key) {
		return db.get(key);
	}
	
	public String remove(String key) {
		return db.remove(key);
	}
	
	public void reset() {
		db.reset();
	}
	
	public void close() {
		db.close();
	}
	
}
