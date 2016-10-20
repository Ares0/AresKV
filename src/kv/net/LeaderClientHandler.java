package kv.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.bean.RemoteACK;
import kv.cluster.Leader;
import kv.net.util.DuplicateClientHandler;


/**
 *  LeaderClient
 * 主服务器重启，加入集群。
 * */
public class LeaderClientHandler extends ChannelInboundHandlerAdapter implements DuplicateClientHandler {

	private Leader leader;
	
	public LeaderClientHandler(Leader leader) {
		this.leader = leader;
	}
	
	public void channelActive(ChannelHandlerContext ctx) 
			throws Exception {
		leader.doActive(ctx);
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			leader.doRead(ctx, (RemoteACK)msg);
		} catch(Exception e) {
			System.out.println("master wrong ackMsg" + e.toString());
		}
		
		msg = null;
	}

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
    	ctx.close();
    	System.out.println("leader client con exception");
    }
    
}
