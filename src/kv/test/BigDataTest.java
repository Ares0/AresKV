package kv.test;

import kv.db.KVConnection;
import kv.db.KVDataBase;

public class BigDataTest {
	
	static KVDataBase db = KVDataBase.getDatabase();
	static KVConnection con = db.getConnection();

	public static void main(String[] args) {
		test1();
		test11();
	}
	
	private static void test1() {

		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			con.put("" + i, "" + i);
		}
		System.out.println("一百万写：" + (System.currentTimeMillis() - time1));
		
		long time2 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			con.get("" + i);
			con.get("" + i);
			con.get("" + i);
			con.get("" + i);
			con.get("" + i);
		}
		System.out.println("五百万读：" + (System.currentTimeMillis() - time2));
	}
	
	private static void test11() {
		long time3 = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			con.put("" + i, "" + i);
		}
		System.out.println("一千万写：" + (System.currentTimeMillis() - time3));
		
		long time4 = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			con.get("" + i);
			con.get("" + i);
			con.get("" + i);
			con.get("" + i);
			con.get("" + i);
		}
		System.out.println("五千万读：" + (System.currentTimeMillis() - time4));
	}
	
}
