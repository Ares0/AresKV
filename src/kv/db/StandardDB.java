package kv.db;

import kv.Command;
import kv.db.handler.DataHandler;
import kv.db.handler.ExpireHandler;
import kv.db.handler.WatchHandler;
import kv.db.log.Dumper;
import kv.net.Connector;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestQueue;
import kv.queue.ResponseLinkedQueue;
import kv.queue.ResponseQueue;
import kv.synchro.SpinSynchronous;
import kv.synchro.Synchronous;

/**
 *  standlone
 * */
public class StandardDB extends AbstractDB{

	StandardDB(int initCapacity) {
		this(initCapacity, new RequestLinkedQueue(), new ResponseLinkedQueue(), new SpinSynchronous());
	}
	
	StandardDB(int initCapacity, RequestQueue req,
			ResponseQueue rep, Synchronous syn) {
		clientId = 1;
		dump = new Dumper(this);
		connector = new Connector(this);
		
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
			
			if (req.getCommand() == Command.PUT || req.getCommand() == Command.GET
					|| req.getCommand() == Command.REMOVE || req.getCommand() == Command.RESET) {
				handler.process(req);
			} else if (req.getCommand() == Command.CLOSE) {
				handler.process(req);
				this.stop();
			} else {
				System.out.println("wrong request type " + req.getCommand());
			}
		}
		System.out.println("db stop " + spinCount);
	}

}
