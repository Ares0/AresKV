package kv.test;


import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.Command;
import kv.Type;
import kv.net.RemoteRequest;
import kv.net.RemoteResponse;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	        RemoteRequest<String, String> rq = new RemoteRequest<String, String>(Command.PUT,
	        		Type.STRING_TYPE, Type.STRING_TYPE, "1", "1", InetAddress.getLocalHost().toString());
	        ctx.writeAndFlush(rq);
	        System.out.println("client active");
    }

    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	System.out.println("client read");
    	
    	System.out.println(((RemoteResponse<String, String>)msg).getKey());
        System.out.println(((RemoteResponse<String, String>)msg).getValue());
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }
	    
}
