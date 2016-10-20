package kv.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.cluster.Follower;
import kv.net.util.DuplicateServerHandler;


/**
 *  FollowerServer
 * ���ڵ�ʧ�ܣ�����ѡ�١�
 * */
public class FollowerServerHandler extends ChannelInboundHandlerAdapter implements DuplicateServerHandler {

	private Follower follower;
	
	public FollowerServerHandler(Follower follower) {
		this.follower = follower;
	}
	
	public void channelActive(ChannelHandlerContext ctx) 
			throws Exception {
		follower.doActive(ctx);
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
		follower.doRead(ctx, msg);
	}

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
    	ctx.close();
    	System.out.println("follower server con exception");
    }
    
}
