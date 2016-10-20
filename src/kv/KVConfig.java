package kv;

import kv.db.KVDataBase;

public class KVConfig {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		KVDataBase db = KVDataBase.getDatabase();
		try {
			db.start();
		} catch (InterruptedException e) {
			db.stop();
			e.printStackTrace();
		}
	}
	
}
