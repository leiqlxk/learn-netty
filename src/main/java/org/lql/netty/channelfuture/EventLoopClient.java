package org.lql.netty.channelfuture;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.Future;

/**
 * Title: EventLoopClient <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 19:58 <br>
 */
public class EventLoopClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoopClient.class);

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup nioGroup = new NioEventLoopGroup();

        // 带有Future、Promise的类型一般都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(nioGroup)
                .channel(NioSocketChannel.class)
                .handler((new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        channel.pipeline().addLast(new StringEncoder());
                    }
                }))
                // 异步非阻塞，main 发起调用，真正执行 connect 是nio线程
                .connect(new InetSocketAddress("localhost", 9000));

        // 如果不调用sync等待连接返回结果就去写入数据，不一定连接成功，因此数据发送不一定会发出
        // 1.使用sync 方法同步处理结果，即阻塞住，知道nio建立连接完毕
//        channelFuture.sync();
        // 连接未返回，channel自然也不能建立成功
//        Channel channel = channelFuture.channel();
//        LOGGER.debug("{}", channel);

//        channel.writeAndFlush("hello, world");

        // 2. 使用addListener(回调对象)方法异步处理结果
        /*channelFuture.addListener(new ChannelFutureListener() {
            @Override
            // 在nio线程连接建立好之后会调用该方法
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                LOGGER.debug("{}", channel);
                channel.writeAndFlush("hello, world");
            }
        });*/

        channelFuture.sync();
        Channel channel = channelFuture.channel();
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();

                if ("q".equalsIgnoreCase(line)) {
                    // close也是异步操作
                    channel.close();

                    // 由于close也是异步操作，所以此处也不能保证是在关闭连接后运行
//                    LOGGER.debug("处理关闭之后的操作");
                    break;
                }

                channel.writeAndFlush(line);
            }
        }, "input").start();

        // 放在此处并不会在最后执行
//        LOGGER.debug("处理关闭之后的操作");

        // 获取 CloseFuture对象 1. 同步处理关闭 2. 异步处理关闭
        ChannelFuture closeFuture = channel.closeFuture();
        /*System.out.println("wating close...");
        closeFuture.sync();
        LOGGER.debug("处理关闭之后的操作");*/
        closeFuture.addListener((ChannelFutureListener) channelFuture1 -> {
            LOGGER.debug("处理关闭之后的操作");
            // 优雅关闭
            nioGroup.shutdownGracefully();
        });
    }
}
