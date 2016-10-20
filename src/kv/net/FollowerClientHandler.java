package kv.net;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.cluster.Follower;
import kv.net.util.DuplicateClientHandler;


/**
 *  FollowerClient
 * tcp长连接，连接服务端。
 * */
@Sharable
public class FollowerClientHandler extends ChannelInboundHandlerAdapter implements DuplicateClientHandler {

	private Follower follower;
	
	public FollowerClientHandler(Follower follower) {
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
        System.out.println("follower client con exception");
    }
    
    public void channelRegistered(ChannelHandlerContext ctx) {
    	follower.registeChannel(ctx.channel());
    }
    
}
