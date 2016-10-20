package kv.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import kv.KVDataBase;

// Connector
public class Connector implements Runnable{

	private int port;
	
	private static int DEFAULT_PORT = 8075;
	
	private static int DEFAULT_OBJECT_SIZE = 4*1024*1024;
	
	private EventLoopGroup acceptorGroup;
	
	private EventLoopGroup workerGroup;
	
	private NetHandler nh;
	
	private Thread connectorThread;
	
	private ServerBootstrap bootstrap;
	
	public Connector(KVDataBase db) {
		this.port = DEFAULT_PORT;
		acceptorGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        nh = new NetHandler(db);
        bootstrap = new ServerBootstrap();
	}
	
	public void start() throws InterruptedException {
		connectorThread = new Thread(this);
		connectorThread.start();
	}
	
	// run»á½áÊø
	public void stop() {
		acceptorGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        
        System.out.println("connector stop");
	}

	@Override
	public void run() {
		try {
            bootstrap.group(acceptorGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
//                   .option(ChannelOption.SO_BACKLOG, 100)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
                        	ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        	ch.pipeline().addLast(new ObjectDecoder(DEFAULT_OBJECT_SIZE, ClassResolvers.weakCachingConcurrentResolver(
                                      				this.getClass().getClassLoader())));
                        	ch.pipeline().addLast(new ObjectEncoder());
                        	ch.pipeline().addLast(nh);
                        }
                    });

            System.out.println("connector start");
            
            ChannelFuture cf = bootstrap.bind(port).sync();
            
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			stop();
        }
	}
	
}
