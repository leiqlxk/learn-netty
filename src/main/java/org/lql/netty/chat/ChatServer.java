package org.lql.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.lql.netty.chat.handler.*;
import org.lql.netty.chat.protocol.MessageCodecSharable;
import org.lql.netty.chat.protocol.ProcotolFreameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: ChatServer <br>
 * ProjectName: learn-netty <br>
 * description: 聊天服务器 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 21:46 <br>
 */
public class ChatServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler createRequestMessageHandler = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler groupChatRequestMessageHandler = new GroupChatRequestMessageHandler();
        GroupJoinRequestMessageHandler joinRequestMessageHandler = new GroupJoinRequestMessageHandler();
        GroupMemebersRequestMessageHandler memebersRequestMessageHandler = new GroupMemebersRequestMessageHandler();
        GroupQuitRequestMessageHandler quitRequestMessageHandler = new GroupQuitRequestMessageHandler();
        QuitHandler quitHandler = new QuitHandler();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ProcotolFreameDecoder());
//                            channel.pipeline().addLast(loggingHandler);
                            channel.pipeline().addLast(messageCodec);
                            // 空闲检测器，用来判断是不是读空闲时间过长，或写空闲时间过长，超过了会触发IdleStateEvent事件的三种状态：IdleState.READER_IDLE/ IdleState.WRITER_IDLE / IdleState.ALL_IDLE
                            // 参数1 检测读空闲
                            // 参数2 检测写空闲
                            // 参数3 检测读和写时间总和检测
                            channel.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                            // ChannelDuplexHandler 可以同时作为入站和出站处理器
                            channel.pipeline().addLast(new ChannelDuplexHandler() {
                                // 用来触发特殊事件
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    if ((event.state() == IdleState.READER_IDLE)) {
                                        LOGGER.debug("已经 10s 没有读到数据");
                                        // 释放资源
                                        ctx.channel().close();
                                    }
                                }
                            });

                            // 处理loggin消息的handler
                            channel.pipeline().addLast(loginRequestMessageHandler);
                            // 处理单聊
                            channel.pipeline().addLast(chatRequestMessageHandler);
                            channel.pipeline().addLast(createRequestMessageHandler);
                            channel.pipeline().addLast(groupChatRequestMessageHandler);
                            channel.pipeline().addLast(joinRequestMessageHandler);
                            channel.pipeline().addLast(memebersRequestMessageHandler);
                            channel.pipeline().addLast(quitRequestMessageHandler);
                            channel.pipeline().addLast(quitHandler);
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(9000).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("server error", e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
