package kv;

import kv.db.AbstractDB.Iterator;
import kv.net.KVConnection;
import kv.queue.RequestQueue;
import kv.queue.ResponseQueue;

public interface KVDataBase extends Runnable{

	public void start() throws InterruptedException;
	
	public void stop();

	public RequestQueue getRequestQueue();

	public ResponseQueue getResponseQueue();

	public long getClientId();

	public Iterator getIterator();

	public KVConnection getConnection();

}
