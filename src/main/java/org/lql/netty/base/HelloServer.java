package org.lql.netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Title: HelloServer <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/10 23:14 <br>
 */
public class HelloServer {

    public static void main(String[] args) {
        // 服务器端启动器 负责组装netty组件，启动服务器
        new ServerBootstrap()
                // EventLoop参考nio多线程优化版，它其中包含一个selector和一个thread
                .group(new NioEventLoopGroup())
                // netty支持NIO、BIO、OIO等，选择服务器的ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)
                // nio多线程优化版的work(child) 负责处理读写，决定了worker(child)能执行哪些操作（handler）
                .childHandler(
                        // channel代表和客户端进行数据读写的通道 Initializer 初始化器，负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                       // 添加 handler
                        // 将ByteBuf转换为字符串
                        nioSocketChannel.pipeline().addLast(new StringDecoder());

                        // 自定义handler
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                           // 读事件
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 打印转换后的字符串
                                System.out.println(msg);
                            }
                        });
                    }
                }).bind(9000);
    }
}
