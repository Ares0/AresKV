package kv.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import kv.db.KVDataBase;


// NetHandler
public class NetHandler<K, V> extends ChannelInboundHandlerAdapter {

	private KVDataBase<K, V> db;

	public NetHandler(KVDataBase<K, V> db) {
		this.db = db;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}	
	
	@SuppressWarnings("unchecked")
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		System.out.println("read req " + msg.toString());
		
		RemoteRequest<K, V> req = (RemoteRequest<K, V>) msg;
		RemoteResponse<K, V> rep = db.getConnection().process(req);
		ctx.writeAndFlush(rep);
		
		System.out.println("write rep " + rep.getKey());
	}

}
