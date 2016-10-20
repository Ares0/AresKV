package kv.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import kv.KVDataBase;
import kv.bean.RemoteRequest;
import kv.bean.RemoteResponse;
import kv.utils.Utils;


// NetHandler
public class KVNetHandler extends ChannelInboundHandlerAdapter {

	private KVDataBase db;

	public KVNetHandler(KVDataBase db) {
		this.db = db;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}	
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RemoteRequest req = (RemoteRequest) Utils.getJsonObject(msg.toString(), RemoteRequest.class);
		
		RemoteResponse rep = db.getConnection().process(req);
		
		byte[] buf = Utils.getObjectJson(rep).getBytes();
    	
    	ByteBuf pool = PooledByteBufAllocator.DEFAULT.directBuffer(buf.length);
    	pool.writeBytes(buf);
    	pool.retain();
    	
    	ctx.writeAndFlush(pool);
    	
    	ReferenceCountUtil.release(pool);
    	rep = null;  // remote gc
    	req = null;  // remote gc
	}

}
