package kv.test;

import kv.db.KVDataBase;

public class DBTest {

	public static void main(String[] args) {
		KVDataBase db = KVDataBase.getDatabase();
		db.put("0", "1");
		db.put("1", "1");
		
		db.get("1");
		
		db.put("2", "1");
		db.put("3", "1");
		db.put("4", "1");
		db.put("5", "1");
		db.put("7", "1");
		db.put("8", "1");
		db.put("9", "1");
		
		db.remove("67");
		
		db.put("27", "1");
		db.put("17", "1");
		db.put("37", "1");
		db.put("47", "1");
		db.put("17", "2");
		db.put("57", "2");
		db.put("67", "2");
		
		for (int i = 0; i < 20; i++) {
			db.get("17");
		}
		
		System.out.println(db.get("17"));
	}
	
}
