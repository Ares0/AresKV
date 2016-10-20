package kv.net.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import kv.cluster.Duplicater;

/**
 *  客户端连接器
 * */
public class DubClientConnector implements Runnable {
	
	private int port;

	private String host;
	
	private DuplicateClientHandler dch;
	
	private static int DEFALULT_THREAD = 1;
	
	private static int DEFALULT_SLEEP_TIME = 1000;
	
	private EventLoopGroup eventLoopGroup;

	private Thread connectorThread;
	
	public DubClientConnector(Duplicater duplicater, DuplicateClientHandler dch, String host, int port) {
		this.dch = dch;
		this.port = port;
		this.host = host;
		
		eventLoopGroup = new NioEventLoopGroup(DEFALULT_THREAD);
	}
	
	public void start() {
		connectorThread = new Thread(this);
		connectorThread.start();
		System.out.println("client connector start");
	}
	
	public void stop() {
		eventLoopGroup.shutdownGracefully();
        System.out.println("client connector stop");
	}

	public void run() {
		this.connect();
	}
	
	public void connect() {
		Bootstrap bootstrap = new Bootstrap();
        ChannelFuture future = null;
        
        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
//                        	ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new ObjectDecoder(1024 ,ClassResolvers.cacheDisabled(null)));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(dch);
                        }
                    });

			future = bootstrap.connect(host, port).sync();

        } catch (InterruptedException e) {
        	e.printStackTrace();
        } catch (Exception e) {
        	future = reTryConnect(bootstrap);
        }
        
        try {
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.stop();
        }
        
	}
	
	private ChannelFuture reTryConnect(Bootstrap bootstrap) {
		try {
			Thread.sleep(DEFALULT_SLEEP_TIME);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("retry connect........");
		
		ChannelFuture future = null;
		try {
			future = bootstrap.connect(host, port).sync();
		} catch (Exception e) {
			reTryConnect(bootstrap);
		}
		return future;
	}
	
}
