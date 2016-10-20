package kv.cluster;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import kv.bean.DbRequest;
import kv.bean.RemoteACK;
import kv.bean.RemoteRequest;
import kv.db.MasterSlaveDB;
import kv.net.LeaderServerHandler;
import kv.net.util.DubServerConnector;
import kv.synchro.Synchronous;


/**
 *  leader
 * 服务端监听，绑定Channel；
 * 单独的线程遍历ctx来写入数据，暂不处理重新复制。
 * 不一定select哪个worker，但channel（连接）是确实存在的。
 * */
public class Leader implements Duplicater, Runnable {
	
	private MasterSlaveDB db;
	
	private DubServerConnector dsc;  
	
	private Map<String, Channel> cfm;
	
	private Map<String, Long> slaveTick;
	
	private long tickTime;
	
	private static long DEFAULT_TICK_TIME = 2000;
	
	private static RemoteACK ackMsg;
	
	private int port;

	private static int DEFAULT_PORT = 8076;
	
	private String host;
	
	private static String DEFAULT_FOLLOWER_HOST = "127.0.0.1";
	
	private Synchronous syn;
	
	private Thread leaderTh;

	private volatile boolean isRunning;
	
	private Thread slaveTracker;
	
	private LeaderServerHandler lsh; 
	
	public Leader(MasterSlaveDB db, Synchronous syn) {
		this.db = db;
		this.port = DEFAULT_PORT;
		this.host = DEFAULT_FOLLOWER_HOST;
		
		lsh = new LeaderServerHandler(this);
		dsc = new DubServerConnector(this, lsh, port);
		
		this.syn = syn;
		
		cfm = new ConcurrentHashMap<>();
		ackMsg = new RemoteACK(host);
		
		tickTime = DEFAULT_TICK_TIME;
		slaveTick = new ConcurrentHashMap<>();
		
		leaderTh = new Thread(this);
		slaveTracker = new Thread(new SlaveTracker());
	}
	
	public void start() {
		dsc.start();
		
		isRunning = true;
		leaderTh.start();
		
		slaveTracker.start();
		System.out.println("leader start");
	}
	
	public void stop() {
		isRunning = false;
		dsc.stop();
		System.out.println("leader stop");
	}
	
	public void doActive(ChannelHandlerContext ctx) {
		// no-op
		System.out.println("master active " + ctx.channel().toString());
	}

	public void doRead(ChannelHandlerContext ctx, RemoteACK msg) {
		String host = msg.getHost();
		slaveTick.put(host, System.currentTimeMillis());
		
		if (cfm.get(host) == null) {
			cfm.put(host, ctx.channel());
		}
		
		if (msg.isRep()) {
			ctx.writeAndFlush(ackMsg);
		}
	}

	// client 写入
	public void run() {
		while (isRunning) {
			DbRequest dr;
			while ((dr = db.getDepRequests().consume()) == null) {
				syn.doSynchronous();
			}
			
			RemoteRequest rq = new RemoteRequest(dr.getCommand(),
					dr.getKey(), dr.getValue(), dr.getClientId());
			
			for (Channel cc : cfm.values()) {
				if (cc.isWritable()) {
					cc.writeAndFlush(rq);
				}
			}
		}
	}
	
	// SlaveTracker
	private class SlaveTracker implements Runnable {
		public void run() {
			while (isRunning) {
				try {
					Thread.sleep(tickTime);
				} catch (InterruptedException e1) {
					if (!isRunning) {
						return;
					}
				}
				
				long current = System.currentTimeMillis();
				for (Entry<String, Long> e : slaveTick.entrySet()) {
					if (e != null) {
						if (current - e.getValue() > tickTime) {
							Channel cc;
							String key = e.getKey();
							if ((cc = cfm.get(key)) != null) {
								cfm.remove(key);
								slaveTick.remove(key);
								cc.close();  // close
							}
						}
					}
				}
			}
		}
	}

}
