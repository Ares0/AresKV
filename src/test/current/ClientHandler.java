package test.current;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.Command;
import kv.bean.RemoteRequest;
import kv.utils.DataType;
import kv.utils.KVObject;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	long time = System.currentTimeMillis();
	
	int sum = 1000000;
	
	int repNum = 0;
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		KVObject val = new KVObject();
		val.setType(DataType.STRING_TYPE);
		val.setValue("1");
		
		RemoteRequest rq = new RemoteRequest(Command.PUT, "1", val, 1);
		
		for (int i = 0; i < sum; i++) {
			ctx.writeAndFlush(rq);
		}
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
//    	System.out.println(((RemoteResponse)msg).getKey());
//        System.out.println(((RemoteResponse)msg).getValue());
    	
    	if (++repNum >= sum) {
    		System.out.println(System.currentTimeMillis() - time);
    	}
    	
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }
	    
}
