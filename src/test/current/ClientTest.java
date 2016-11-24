package test.current;

import java.net.UnknownHostException;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 *  连接小缓冲区，
 * 使用堆外复用内存，及时flush。
 * */ 
public class ClientTest {
	
	private String host;
	
	private int port;
	
	public ClientTest() {
		port = 8075;
		host = "127.0.0.1";
	}
	
	public static void main(String[] args) throws InterruptedException {
		ClientTest cs = new ClientTest();
		try {
			cs.remote();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void remote() throws InterruptedException, UnknownHostException {
		Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
//                        	ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new LineBasedFrameDecoder(4096));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
	}
	
}
