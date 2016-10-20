package test.current;


import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.Command;
import kv.net.RemoteRequest;
import kv.net.RemoteResponse;
import kv.utils.KVObject;
import kv.utils.Type;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		KVObject val = new KVObject();
		val.setType(Type.STRING_TYPE);
		val.setValue("1");
		
        RemoteRequest rq = new RemoteRequest(Command.PUT,
        		Type.STRING_TYPE, Type.STRING_TYPE, "1", val, InetAddress.getLocalHost().toString());
        ctx.writeAndFlush(rq);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	System.out.println(((RemoteResponse)msg).getKey());
        System.out.println(((RemoteResponse)msg).getValue());
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }
	    
}
