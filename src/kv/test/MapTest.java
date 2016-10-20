package kv.test;

import kv.db.KVMap;

public class MapTest {

	public static void main(String[] args) {
		KVMap<String, String> m = new KVMap<>();
		m.put("1", "1");
		m.put("2", "1");
		m.put("3", "1");
		m.put("4", "1");
		m.put("5", "1");
		m.put("7", "1");
		m.put("27", "1");
		m.put("17", "1");
		
		m.remove("17");
		
		m.put("37", "1");
		m.put("47", "1");
		m.put("17", "2");
		m.put("57", "2");
		m.put("67", "2");
		m.put("77", "2");
		m.put("87", "2");
		m.put("13", "2");
		
		for (int i = 0; i < 20; i++) {
			m.remove(""+i);
		}
		
		System.out.println(m.get("17"));
		System.out.println(m.get("37"));
	}

}
