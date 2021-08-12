package org.lql.netty.advance.halfblag;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Title: HelloWorldClient <br>
 * ProjectName: learn-netty <br>
 * description: 使用短连接解决黏包 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/12 20:00 <br>
 */
public class HelloWorldClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldClient.class);

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            send();
        }
        System.out.println("finish....");
    }

    private static void send(){
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                // 会在连接channel建立成功后触发active
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                   /* ByteBuf buf = null;
                                    for (int i = 0; i < 10; i++) {
                                        buf = ctx.alloc().buffer(16);
                                        buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                        ctx.writeAndFlush(buf);
                                    }*/

                                    // 短连接的方式可以有效的解决黏包的问题。但是受服务端接收缓冲区大小影响，还是会出现半包
                                    ByteBuf buf = ctx.alloc().buffer(16);
                                    buf.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17});
                                    ctx.writeAndFlush(buf);
                                    // 使用短连接来解决黏包、半包的问题
                                    ctx.channel().close();

                                    buf.release();
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 9000)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
