package kv.cluster;

import io.netty.channel.ChannelHandlerContext;

/**
 * ¸´ÖÆ
 * */
public interface Duplicater {

	void doActive(ChannelHandlerContext ctx);
	
	void start();

	void stop();

}
