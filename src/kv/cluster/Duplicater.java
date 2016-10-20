package kv.cluster;

import io.netty.channel.ChannelHandlerContext;

public interface Duplicater {

	void doActive(ChannelHandlerContext ctx);
	
	void start();

	void stop();

}
