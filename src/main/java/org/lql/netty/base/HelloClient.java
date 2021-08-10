package org.lql.netty.base;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Title: HelloClient <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/10 23:26 <br>
 */
public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        // 启动器类
        new Bootstrap()
                // 添加EventLoop
                .group(new NioEventLoopGroup())
                // 选择客户端channel实现
                .channel(NioSocketChannel.class)
                // 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 连接到服务器
                .connect(new InetSocketAddress("localhost", 9000))
                // 阻塞方法，直到连接建立
                .sync()
                // 代表了连接对象
                .channel()
                // 像服务器发送数据
                .writeAndFlush("hello world");
    }
}
