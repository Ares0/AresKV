package kv.db;

import kv.Command;
import kv.bean.DbRequest;
import kv.db.handler.DataHandler;
import kv.db.handler.ExpireHandler;
import kv.db.handler.WatchHandler;
import kv.db.log.Dumper;
import kv.net.KVConnector;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestQueue;
import kv.queue.ResponseLinkedQueue;
import kv.queue.ResponseQueue;
import kv.synchro.Synchronous;
import kv.synchro.SynchronousFactory;

/**
 *  standlone
 * */
public class StandAloneDB extends AbstractDB{

	StandAloneDB(int initCapacity) {
		this(initCapacity, new RequestLinkedQueue(), 
				new ResponseLinkedQueue(), SynchronousFactory.getSpinSynchronous());
	}
	
	StandAloneDB(int initCapacity, RequestQueue req,
			ResponseQueue rep, Synchronous syn) {
		clientId = 1;
		dump = new Dumper(this);
		connector = new KVConnector(this);
		
		this.syn = syn;
		this.requests = req;
		this.responses = rep;
		
		this.isRunning = true;
	}

	protected void prepareHandlers() {
		handler = new ExpireHandler();
		handler.setDataBase(this);
		
		WatchHandler wh = new WatchHandler();
		wh.setDataBase(this);
		handler.setNextHandler(wh);
		
		DataHandler dh = new DataHandler();
		dh.setDataBase(this);
		wh.setNextHandler(dh);
	}

	@Override
	public void run() {
		while (isRunning) {
			DbRequest req;
			while ((req = requests.consume()) == null) {
				syn.doSynchronous();
				spinCount++;
			}
			
			Command com = req.getCommand();
			if (com == Command.PUT || com == Command.GET
					|| com == Command.REMOVE || com == Command.RESET) {
				handler.process(req);
			} else if (com == Command.CLOSE) {
				handler.process(req);
				this.stop();
			} else {
				System.out.println("wrong request type " + req.getCommand());
			}
			req = null;  // gc db req except expire and watch
		}
		System.out.println("db stop " + spinCount);
	}

}
