package kv.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import kv.Command;
import kv.bean.DbRequest;
import kv.db.handler.ClusterHandler;
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
import kv.utils.Range;

/*
 * cluster
 * **/ 
public class ClusterDB extends AbstractDB {
	
	// 本节点范围
	private Range keyRange;
	
	// 服务器对应范围
	private Map<String, Range> clusterRange;
	
	// 范围对应服务器
	private Map<Range, String> rangeCluster;
	
	public static int KEY_RANGE_MAX_VALUE = 16384;
	
	ClusterDB(int keyStart, int keyEnd) {
		this(keyStart, keyEnd, new RequestLinkedQueue(), new ResponseLinkedQueue(), 
				SynchronousFactory.getSpinSynchronous(), null);
	}
	
	ClusterDB(Range range, Map<String, Range> clusterRouter) {
		this(range.getStart(), range.getEnd(), new RequestLinkedQueue(), new ResponseLinkedQueue(), 
				SynchronousFactory.getSpinSynchronous(), clusterRouter);
	}
	
	ClusterDB(int keyStart, int keyEnd, RequestQueue req,
			ResponseQueue rep, Synchronous syn, Map<String, Range> clusterRange) {
		clientId = 1;
		dump = new Dumper(this);
		connector = new KVConnector(this);
		
		this.syn = syn;
		this.requests = req;
		this.responses = rep;
		
		this.keyRange = new Range(keyStart, keyEnd);
		if (clusterRange != null) {
			this.clusterRange = clusterRange;
			initRangeCluster();
		} else {
			clusterRange = new HashMap<String, Range>();
			rangeCluster = new TreeMap<>();
		}
		
		this.isRunning = true;
	}
	
	private void initRangeCluster() {
		rangeCluster = new TreeMap<>();
		for (Entry<String, Range> e : clusterRange.entrySet()) {
			rangeCluster.put(e.getValue(), e.getKey());
		}
	}

	public Range getKeyRange() {
		return keyRange;
	}

	public Map<String, Range> getClusterRange() {
		return clusterRange;
	}

	public Map<Range, String> getRangeCluster() {
		return rangeCluster;
	}

	protected void prepareHandlers() {
		handler = new ClusterHandler();
		handler.setDataBase(this);
		
		ExpireHandler eh = new ExpireHandler();
		eh.setDataBase(this);
		handler.setNextHandler(eh);
		
		WatchHandler wh = new WatchHandler();
		wh.setDataBase(this);
		eh.setNextHandler(wh);
		
		DataHandler dh = new DataHandler();
		dh.setDataBase(this);
		wh.setNextHandler(dh);
	}
	
	public void run() {
		while (isRunning) {
			DbRequest req;
			while ((req = requests.consume()) == null) {
				syn.doSynchronous();
				spinCount++;
			}
			
			int com = req.getCommand();
			if (com == Command.PUT || com == Command.GET
					|| com == Command.REMOVE || com == Command.RESET
					|| com == Command.ADD_CLUSTER_NODE || com == Command.CHANGE_CLUSTER_RANGE) {
				handler.process(req);
			} else if (com == Command.CLOSE) {
				handler.process(req);
				this.stop();
			} else {
				System.out.println("wrong request type " + req.getCommand());
			}
		}
		System.out.println("cluster node stop " + spinCount);
	}
	
}
