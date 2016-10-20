package kv.net.util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import kv.cluster.Duplicater;

/**
 *  服务端连接器
 * */
public class DubServerConnector implements Runnable {
	
	private int port;

	private DuplicateServerHandler dsh;
	
	private static int DEFALULT_WORK_THREAD = 2;
	
	private static int DEFALULT_ACTTPTOR_THREAD = 1;
	
	private static int DEFAULT_OBJECT_SIZE = 1*1024;
	
	private ServerBootstrap bootstrap;
	
	private EventLoopGroup acceptorGroup;
	
	private EventLoopGroup workerGroup;
	
	private Thread connectorThread;

	public DubServerConnector(Duplicater duplicater, DuplicateServerHandler dsh, int port) {
		this.dsh = dsh;
		this.port = port;
		
		bootstrap = new ServerBootstrap();
		workerGroup = new NioEventLoopGroup(DEFALULT_WORK_THREAD);
		acceptorGroup = new NioEventLoopGroup(DEFALULT_ACTTPTOR_THREAD);
	}
	
	public void start() {
		connectorThread = new Thread(this);
		connectorThread.start();
		System.out.println("server connector start");
	}
	
	public void stop() {
		acceptorGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
        System.out.println("server connector stop");
	}

	public void run() {
		this.binding();
	}
	
	public void binding() {
        try {
            bootstrap.group(acceptorGroup, workerGroup)
		             .channel(NioServerSocketChannel.class)
		             .option(ChannelOption.TCP_NODELAY, true)
		             .childOption(ChannelOption.SO_KEEPALIVE, true)
		             .childHandler(new ChannelInitializer<SocketChannel>() {
		                public void initChannel(SocketChannel ch) throws Exception {
//		               	 ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
		               	 ch.pipeline().addLast(new ObjectDecoder(DEFAULT_OBJECT_SIZE, ClassResolvers.weakCachingConcurrentResolver(
		                             				this.getClass().getClassLoader())));
		               	 ch.pipeline().addLast(new ObjectEncoder());
		               	 ch.pipeline().addLast(dsh);
		                }
		             });

            ChannelFuture future = bootstrap.bind(port).sync();

			future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
        	e.printStackTrace();
        } finally {
        	this.stop();
        }
	}

}
