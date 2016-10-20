package kv.db;

import java.util.ArrayList;
import java.util.List;

import kv.Command;
import kv.bean.DbRequest;
import kv.cluster.Duplicater;
import kv.cluster.Follower;
import kv.cluster.Leader;
import kv.db.handler.DataHandler;
import kv.db.handler.ExpireHandler;
import kv.db.handler.FollowerHandler;
import kv.db.handler.LeaderHandler;
import kv.db.handler.WatchHandler;
import kv.db.log.Dumper;
import kv.net.KVConnector;
import kv.queue.RequestLinkedQueue;
import kv.queue.RequestQueue;
import kv.queue.ResponseLinkedQueue;
import kv.queue.ResponseQueue;
import kv.synchro.Synchronous;
import kv.synchro.SynchronousFactory;

public class MasterSlaveDB extends AbstractDB {

	private long txid; // 复制序号
	
	private RequestQueue depRequests; // 复制队列
	
	private volatile DBState state;  // 状态
	
	private Duplicater dupter;
	
	public enum DBState {
		LOOKING, LEADERING, FOLLOWERING
	}
	
	private List<String> slaveAddress;
	
	MasterSlaveDB() {
		this(new RequestLinkedQueue(), new ResponseLinkedQueue(), 
				SynchronousFactory.getSpinSynchronous(), new RequestLinkedQueue(), null, DBState.FOLLOWERING);
	}
	
	MasterSlaveDB(List<String> sa, DBState state) {
		this(new RequestLinkedQueue(), new ResponseLinkedQueue(), 
				SynchronousFactory.getSpinSynchronous(), new RequestLinkedQueue(), sa, state);
	}
	
	MasterSlaveDB(RequestQueue req,
			ResponseQueue rep, Synchronous syn, RequestQueue dup, List<String> sa, DBState state) {
		clientId = 1;
		dump = new Dumper(this);
		connector = new KVConnector(this);
		
		this.syn = syn;
		this.requests = req;
		this.responses = rep;
		
		this.txid = 0;
		this.state = state;
		this.depRequests = dup;
		
		if (sa == null) {
			this.slaveAddress = new ArrayList<>();
		} else {
			this.slaveAddress = sa;
		}
		
		if (state == DBState.LEADERING) {
			this.dupter = new Leader(this, SynchronousFactory.getSleepSynchronous());
		} else {
			this.dupter = new Follower(this);
		}
		
		this.isRunning = true;
	}
	
	public DBState getState() {
		return state;
	}

	public void setState(DBState state) {
		this.state = state;
	}

	public long getTxid() {
		return txid;
	}

	public void setTxid(long txid) {
		this.txid = txid;
	}

	public List<String> getSlaveAddress() {
		return slaveAddress;
	}

	public RequestQueue getDepRequests() {
		return depRequests;
	}

	public void setDepRequests(RequestQueue depRequests) {
		this.depRequests = depRequests;
	}

	public void start() throws InterruptedException {
		prepareHandlers();
		
		dbTh = new Thread(this);
		dbTh.setName("db-thread");
		dbTh.start();
		
		connector.start();
		dump.start();
		
		dupter.start();
		System.out.println("db start");
	}
	
	protected void prepareHandlers() {
		if (state == DBState.LEADERING) {
			handler = new ExpireHandler();
			handler.setDataBase(this);
			
			WatchHandler wh = new WatchHandler();
			wh.setDataBase(this);
			handler.setNextHandler(wh);
			
			LeaderHandler lh = new LeaderHandler();
			lh.setDataBase(this);
			wh.setNextHandler(lh);
			
			DataHandler dh = new DataHandler();
			dh.setDataBase(this);
			lh.setNextHandler(dh);
		} else if (state == DBState.FOLLOWERING 
				||state == DBState.LOOKING) {
			handler = new WatchHandler();
			handler.setDataBase(this);
			
			FollowerHandler fh = new FollowerHandler();
			fh.setDataBase(this);
			handler.setNextHandler(fh);
			
			DataHandler dh = new DataHandler();
			dh.setDataBase(this);
			fh.setNextHandler(dh);
		}
	}
	
	public void stop() {
		isRunning = false;
		
		dump.stop();
		connector.stop();
		dupter.stop();
		
		requests = null;
		responses = null;
		handler = null;
		depRequests = null;
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
					|| com == Command.REMOVE || com == Command.RESET
					|| com == Command.DIRTY || com == Command.EXPIRE) {
				handler.process(req);
			} else if (com == Command.CLOSE) {
				handler.process(req);
				this.stop();
			} else {
				System.out.println("wrong request type " + req.getCommand());
			}
			txid++;  // 事务序列号
			req = null;  // gc db req except expire and watch
		}
		System.out.println("ms dbe stop " + spinCount);
	}

}
