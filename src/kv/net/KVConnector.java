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
import kv.KVDataBase;

// Connector
public class KVConnector implements Runnable {

	private int port;
	
	private static int DEFAULT_PORT = 8075;
	
	private static int DEFAULT_OBJECT_SIZE = 4*1024*1024;
	
	private EventLoopGroup acceptorGroup;
	
	private EventLoopGroup workerGroup;
	
	private KVNetHandler nh;
	
	private Thread connectorThread;
	
	private ServerBootstrap bootstrap;
	
	public KVConnector(KVDataBase db) {
		this.port = DEFAULT_PORT;
		acceptorGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        nh = new KVNetHandler(db);
        bootstrap = new ServerBootstrap();
	}
	
	public void start() throws InterruptedException {
		connectorThread = new Thread(this);
		connectorThread.setName("Connector-Thread");
		
		connectorThread.start();
		System.out.println("kv connector start");
	}
	
	public void stop() {
		acceptorGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        
        System.out.println("kv connector stop");
	}

	@Override
	public void run() {
		try {
            bootstrap.group(acceptorGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
                        	ch.pipeline().addLast(new ObjectDecoder(DEFAULT_OBJECT_SIZE, ClassResolvers.weakCachingConcurrentResolver(
                                      				this.getClass().getClassLoader())));
                        	ch.pipeline().addLast(new ObjectEncoder());
                        	ch.pipeline().addLast(nh);
                        }
                    });
           
            ChannelFuture cf = bootstrap.bind(port).sync();
            
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			stop();
        }
	}
	
}
