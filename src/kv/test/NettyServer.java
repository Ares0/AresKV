package kv.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyServer {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 5656;
    private static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors()*2;
    
    private static final int BIZTHREADSIZE = 100;
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);
    
    public static void service() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel ch) throws Exception {
               // TODO Auto-generated method stub
                ChannelPipeline pipeline = ch.pipeline();
                 pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                   pipeline.addLast(new LengthFieldPrepender(4));
                    pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                    pipeline.addLast(new TcpServerHandler());
            }
            
        });
       ChannelFuture f = bootstrap.bind(IP, PORT).sync();
       f.channel().closeFuture().sync();
    }

    protected static void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("开始启动TCP服务器...");
        NettyServer.service();
//            HelloServer.shutdown();
    }
    
    public static class TcpServerHandler extends ChannelInboundHandlerAdapter {
        
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            System.out.println("server receive message :"+ msg);
            ctx.channel().writeAndFlush("yes server already accept your message" + msg);
            ctx.close();
        }
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive>>>>>>>>");
        }
          @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("exception is general");
        }
    }
        
}
