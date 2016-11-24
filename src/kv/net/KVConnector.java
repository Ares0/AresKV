package kv.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import kv.KVDataBase;

// Connector
/**
 *  Connector
 * 连接小缓冲区，增加缓冲区，使用堆外内存池。
 * */
public class KVConnector implements Runnable {

	private int port;
	
	private static int DEFAULT_PORT = 8075;
	
	private static int DEFAULT_MAX_LENGTH = 4096;
	
	private static int RCV_BUF_SIZE = 256*1024;
	
	private static int SND_BUF_SIZE = 256*1024;
	
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
                     .option(ChannelOption.SO_RCVBUF, RCV_BUF_SIZE)
                     .option(ChannelOption.SO_SNDBUF, SND_BUF_SIZE)
                     .childOption(ChannelOption.TCP_NODELAY, true)
                     .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch) throws Exception {
//                        	ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        	ch.pipeline().addLast(new LineBasedFrameDecoder(DEFAULT_MAX_LENGTH));
                        	ch.pipeline().addLast(new StringDecoder());
                        	ch.pipeline().addLast(new StringEncoder());
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
