package test.current;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import kv.Command;
import kv.bean.RemoteRequest;
import kv.utils.DataType;
import kv.utils.KVObject;
import kv.utils.Utils;

public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	long time;
	
	int sum = 100000;
	
	int repNum = 0;
	
	KVObject val;
	
	RemoteRequest rq;
	
	public ClientHandler() {
		val = new KVObject();
		val.setT(DataType.STRING_TYPE);
		val.setV("1");
		
		rq = new RemoteRequest(Command.PUT, "1", val, 1);
	}
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		for (int i = 0; i < sum; i++) {
			byte[] buf = Utils.getObjectJson(rq).getBytes();
	    	
	    	ByteBuf pool = PooledByteBufAllocator.DEFAULT.directBuffer(buf.length);
	    	pool.writeBytes(buf);
	    	
	    	pool.retain();
	    	
	    	ctx.writeAndFlush(pool);
	    	
	    	ReferenceCountUtil.release(pool);
		}
		ctx.flush();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	if (time == 0) {
    		time = System.currentTimeMillis();
    	}

    	if (++repNum >= sum) {
    		System.out.println(System.currentTimeMillis() - time);
    	}
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }
	    
}
