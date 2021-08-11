package org.lql.netty.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Title: EventLoopServer <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/11 19:52 <br>
 */
public class EventLoopServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLoopServer.class);

    public static void main(String[] args) {
        // 独立的事件循环组
        EventLoopGroup group = new DefaultEventLoopGroup();

        new ServerBootstrap()
                // 对eventLoopGroup进行指责划分， boss 和 worker
                // 细分1：boss 只负责 ServerSocketChannel 上 accept事件   worker只负责 socketChannel 上的读写
                // 细分2：当worker中有一个io任务处理时间长，那必将影响到别的channel的工作，再创建一个NioEventLoopGroup来处理耗时较长的
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                LOGGER.debug(buf.toString(Charset.defaultCharset()));
                                // 将消息传递给下一个handler
                                ctx.fireChannelRead(msg);
                            }
                        }).addLast(group, "hanlder2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                LOGGER.debug(buf.toString(Charset.defaultCharset()));
                            }
                        });
                    }
                }).bind(9000);
    }
}
