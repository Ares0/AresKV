package kv.cluster;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import kv.bean.RemoteACK;
import kv.bean.RemoteRequest;
import kv.db.MasterSlaveDB;
import kv.net.FollowerClientHandler;
import kv.net.util.DubClientConnector;


/**
 *  Follower
 * 绑定到客户端连接；
 * 内部产生一个ACK线程用于心跳。
 * */
public class Follower implements Duplicater {

	private MasterSlaveDB db;
	
	private DubClientConnector dcc;
	
	private long tickTime;
	
	private long lastTime;
	
	private ACK ack;
	
	private Thread ackTh;
	
	private volatile boolean isRunning;
	
	private static long DEFAULT_TICK_TIME = 1000;
	
	private Channel cc;
	
	private static RemoteACK ackMsg;
	
	private int port;

	private static int DEFAULT_PORT = 8076;
	
	private String host;
	
	private static String DEFAULT_LEADER_HOST = "127.0.0.1";
	
	private FollowerClientHandler fch;
	
	public Follower(MasterSlaveDB db) {
		this.db = db;
		this.port = DEFAULT_PORT;
		this.host = DEFAULT_LEADER_HOST;
		
		fch = new FollowerClientHandler(this);
		dcc = new DubClientConnector(this, fch, host, port);
		
		ackMsg = new RemoteACK(host);
		this.tickTime = DEFAULT_TICK_TIME;
		
		ack = new ACK();
		ackTh = new Thread(ack); 
	}

	public void start() {
		dcc.start();
		isRunning = true;
		
		ackTh.start();
		System.out.println("follower start");
	}

	public void stop() {
		dcc.stop();
	    
		ackTh.interrupt();
		isRunning = false;
		System.out.println("follower stop");
	}

	public void registeChannel(Channel channel) {
		cc = channel;
	}
	
	public void doActive(ChannelHandlerContext ctx) {
		this.lastTime = System.currentTimeMillis();
		
		System.out.println("slave active " + ctx.channel().toString());
	}

	public void doRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof RemoteACK) {
			this.lastTime = System.currentTimeMillis();
		} else if (msg instanceof  RemoteRequest) {
			this.lastTime = System.currentTimeMillis();
			db.getConnection().process((RemoteRequest)msg);
		}
	}
	
	private class ACK implements Runnable {
		public void run() {
			while (isRunning) {
				try {
					Thread.sleep(tickTime);
				} catch (InterruptedException e) {
					if (!isRunning) {
						return;
					}
				}
				
				// reset
				ackMsg.setRep(false);  
				
				if (tickTime - lastTime < 0) {
					ackMsg.setRep(true);
				}
				
				if (cc != null && cc.isWritable()) {
					cc.writeAndFlush(ackMsg);
				}
			}
		}
	}

}
