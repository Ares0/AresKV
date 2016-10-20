package test.current;


import kv.db.DBFactory;
import kv.db.StandAloneDB;
import kv.queue.RequestLinkedQueue;
import kv.queue.ResponseBlockingLinkedQueue;
import kv.synchro.SynchronousFactory;



/*  
 * �̴߳����л������ĵ�ʱ�������
 **/
public class KVConfig {

	public static void main(String[] args) {
		spinTest();
	}
	
	public static void spinTest() {
		StandAloneDB db = DBFactory.getStandardDB();
		try {
			db.start();
		} catch (InterruptedException e) {
			db.stop();
			e.printStackTrace();
		}
	}
	
	public static void conditionTest() {
		StandAloneDB db = DBFactory.getStandardDB(16, new RequestLinkedQueue(), 
				new ResponseBlockingLinkedQueue(), SynchronousFactory.getSpinSynchronous());
		try {
			db.start();
		} catch (InterruptedException e) {
			db.stop();
			e.printStackTrace();
		}
	}
	
}
