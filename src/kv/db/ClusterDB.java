package kv.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import kv.Command;
import kv.db.handler.ClusterHandler;
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
		this(keyStart, keyEnd, new RequestLinkedQueue(), new ResponseLinkedQueue(), new SpinSynchronous(), null);
	}
	
	ClusterDB(Range range, Map<String, Range> clusterRouter) {
		this(range.getStart(), range.getEnd(), new RequestLinkedQueue(), new ResponseLinkedQueue(), new SpinSynchronous(), clusterRouter);
	}
	
	ClusterDB(int keyStart, int keyEnd, RequestQueue req,
			ResponseQueue rep, Synchronous syn, Map<String, Range> clusterRange) {
		clientId = 1;
		dump = new Dumper(this);
		connector = new Connector(this);
		
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
			
			if (req.getCommand() == Command.PUT || req.getCommand() == Command.GET
					|| req.getCommand() == Command.REMOVE || req.getCommand() == Command.RESET
					|| req.getCommand() == Command.ADD_CLUSTER_NODE || req.getCommand() == Command.CHANGE_CLUSTER_RANGE) {
				handler.process(req);
			} else if (req.getCommand() == Command.CLOSE) {
				handler.process(req);
				this.stop();
			} else {
				System.out.println("wrong request type " + req.getCommand());
			}
		}
		System.out.println("cluster node stop " + spinCount);
	}
	
}
