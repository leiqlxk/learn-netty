package org.lql.netty.parametertuning;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * Title: TestConnectionTimeout <br>
 * ProjectName: learn-netty <br>
 * description: 连接超时参数配置 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 9:55 <br>
 */
public class TestConnectionTimeout {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 1. 客户端通过option方法配置参数
        // 2. 服务器端通过option方法配置ServerSocketChannel配置参数
        // 3. 服务器端通过childOption方法给SocketChannel配置参数

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    // 配置连接超时参数，并不一定等待5s之后才会抛出超时异常，有可能在之前就抛出连接被拒绝
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 9000)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
