package test.current;

import java.net.UnknownHostException;

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


/**
 *  连接小缓冲区，增加缓冲区，
 * 使用堆外，复用内存，及时flush。
 * */ 
public class ServerTest {
	
	private int port;
	
	private static int RCV_BUF_SIZE = 256*1024;
	
	private static int SND_BUF_SIZE = 256*1024;
	
	public ServerTest() {
		port = 8075;
	}
	
	public static void main(String[] args) throws InterruptedException {
		ServerTest cs = new ServerTest();
		try {
			cs.remote();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void remote() throws InterruptedException, UnknownHostException {
		ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup acceptorGroup = new NioEventLoopGroup();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(acceptorGroup, eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_RCVBUF, RCV_BUF_SIZE)
                    .option(ChannelOption.SO_SNDBUF, SND_BUF_SIZE)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
//                        	ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new LineBasedFrameDecoder(4096));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } finally {
        	acceptorGroup.shutdownGracefully();
            eventLoopGroup.shutdownGracefully();
        }
	}
	
}
