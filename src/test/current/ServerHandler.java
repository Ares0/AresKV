package test.current;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import kv.bean.RemoteRequest;
import kv.bean.RemoteResponse;
import kv.utils.Utils;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// no-op
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
    	Utils.getJsonObject(msg.toString(), RemoteRequest.class);
    	
    	RemoteResponse rep = new RemoteResponse();
    	
    	byte[] buf = Utils.getObjectJson(rep).getBytes();
    	
    	ByteBuf pool = PooledByteBufAllocator.DEFAULT.directBuffer(buf.length);
    	pool.writeBytes(buf);
    	
    	pool.retain();
    	
    	ctx.writeAndFlush(pool);
    	
    	ReferenceCountUtil.release(pool);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }
	    
}
