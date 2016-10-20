package test.current;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.bean.RemoteACK;

public class ClientHandler2 extends ChannelInboundHandlerAdapter {
	
	public void channelActive(ChannelHandlerContext ctx) 
			throws Exception {
        ctx.writeAndFlush(new RemoteACK("127.0.0.1"));
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	System.out.println(((RemoteACK)msg).isRep());
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }
	    
}
