package test.current;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.Command;
import kv.bean.RemoteRequest;
import kv.bean.RemoteResponse;
import kv.utils.KVObject;
import kv.utils.DataType;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	public void channelActive(ChannelHandlerContext ctx) 
			throws Exception {
		KVObject val = new KVObject();
		val.setType(DataType.STRING_TYPE);
		val.setValue("1");
		
        RemoteRequest rq = new RemoteRequest(Command.PUT, "fafhkd", val, 0);
        ctx.writeAndFlush(rq);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	System.out.println(((RemoteResponse)msg).getKey());
        System.out.println(((RemoteResponse)msg).getValue());
        System.out.println(((RemoteResponse)msg).isMove());
        System.out.println(((RemoteResponse)msg).getValue().getValue());
        ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }
	    
}
