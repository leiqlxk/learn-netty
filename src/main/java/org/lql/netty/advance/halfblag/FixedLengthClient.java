package org.lql.netty.advance.halfblag;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Title: FixedLengthClient <br>
 * ProjectName: learn-netty <br>
 * description: 使用固定长度解决黏包半包 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/12 21:04 <br>
 */
public class FixedLengthClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedLengthClient.class);

    public static void main(String[] args) {
        /*fill10Bytes('1', 5);
        fill10Bytes('2', 2);
        fill10Bytes('3', 10);*/

        send();

        System.out.println("finish....");
    }

    /**
     * 生成固定长度字节数组
     * @param c
     * @param len
     * @return
     */
    public static byte[] fill10Bytes(char c, int len) {
        byte[] bytes = new byte[10];
        for (int i = 0; i < 10; i++) {
            if (i < len) {
                bytes[i] = (byte) c;
            }else {
                bytes[i] = '_';
            }

        }

        System.out.println(new String(bytes));

        return bytes;
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
                            channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                // 会在连接channel建立成功后触发active
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    char c = '0';
                                    Random random = new Random();

                                    for (int i = 0; i < 10; i++) {
                                        byte[] bytes = fill10Bytes(c, random.nextInt(10) + 1);
                                        c++;
                                        buf.writeBytes(bytes);
                                    }

                                    ctx.writeAndFlush(buf);
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
