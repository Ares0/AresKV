package kv.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.KVDataBase;


// NetHandler
public class NetHandler extends ChannelInboundHandlerAdapter {

	private KVDataBase db;

	public NetHandler(KVDataBase db) {
		this.db = db;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}	
	
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		RemoteRequest req = (RemoteRequest) msg;
		RemoteResponse rep = db.getConnection().process(req);
		ctx.writeAndFlush(rep);
	}

}
