package test.current;


import kv.db.DBFactory;
import kv.db.StandAloneDB;
import kv.queue.RequestLinkedQueue;
import kv.queue.ResponseMapQueue;
import kv.synchro.SynchronousFactory;

/*  
 * KVConfig
 **/
public class KVConfig {

	public static void main(String[] args) {
		spinTest();
	}
	
	public static void spinTest() {
		StandAloneDB db = DBFactory.getStandardDB(16, new RequestLinkedQueue(), 
				new ResponseMapQueue(), SynchronousFactory.getSpinSynchronous());
		try {
			db.start();
		} catch (InterruptedException e) {
			db.stop();
			e.printStackTrace();
		}
	}
	
}
