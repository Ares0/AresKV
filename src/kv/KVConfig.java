package kv;

import kv.db.DataBaseFactory;
import kv.db.StandardDB;

public class KVConfig {

	public static void main(String[] args) {
		StandardDB db = DataBaseFactory.getStandardDB();
		try {
			db.start();
		} catch (InterruptedException e) {
			db.stop();
			e.printStackTrace();
		}
	}
	
}
