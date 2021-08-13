package org.lql.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.lql.netty.chat.message.*;
import org.lql.netty.chat.protocol.MessageCodecSharable;
import org.lql.netty.chat.protocol.ProcotolFreameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Title: ChatClient <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 23:02 <br>
 */
public class ChatClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClient.class);
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        // 用于工作线程和输入线程通信，当计数下降为0就向下运行，技术不为0则等待
        CountDownLatch downLatch = new CountDownLatch(1);
        // 用于线程交互
        AtomicBoolean login = new AtomicBoolean(false);
        AtomicBoolean exit = new AtomicBoolean(false);

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ProcotolFreameDecoder());
//                            channel.pipeline().addLast(loggingHandler);
                            channel.pipeline().addLast(messageCodec);
                            // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                            // 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
                            channel.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                            // ChannelDuplexHandler 可以同时作为入站和出站处理器
                            channel.pipeline().addLast(new ChannelDuplexHandler() {
                                // 用来触发特殊事件
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    // 触发了写空闲事件
                                    if (event.state() == IdleState.WRITER_IDLE) {
//                                LOGGER.debug("3s 没有写数据了，发送一个心跳包");
                                        ctx.writeAndFlush(new PingMessage());
                                    }
                                }
                            });
                            channel.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {

                                // 在连接建立后触发 active 事件
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 负责接收用户在控制台的输入，负责向服务器发送各种消息
                                    // 为了不阻塞group，单独开一个线程用于输入
                                    new Thread(() -> {
                                        Scanner scanner = new Scanner(System.in);
                                        System.out.println("请输入用户名：");
                                        String username = scanner.nextLine();
                                        if(exit.get()){
                                            return;
                                        }
                                        System.out.println("请输入密码：");
                                        String password = scanner.nextLine();
                                        if(exit.get()){
                                            return;
                                        }
                                        // 构造消息对象
                                        LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                        // 发送消息
                                        ctx.writeAndFlush(loginRequestMessage);

                                        System.out.println("等待后续操作....");
                                        try {
                                            downLatch.await();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        // 如果登录失败
                                        if (!login.get()) {
                                            ctx.channel().close();
                                            return;
                                        }

                                        while (true) {
                                            // 菜单提示
                                            System.out.println("=====================================");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gsend [group name] [content]");
                                            System.out.println("gcreate [group name] [m1,m2,m3,m4...]");
                                            System.out.println("gmembers [group name]");
                                            System.out.println("gjoin [group name]");
                                            System.out.println("gquit [group name]");
                                            System.out.println("quit");
                                            System.out.println("=====================================");

                                            String command = scanner.nextLine();
                                            if(exit.get()){
                                                return;
                                            }
                                            String[] s = command.split(" ");
                                            switch (s[0]) {
                                                case "send":
                                                    ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                                    break;
                                                case "gsend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                                    break;
                                                case "gcreate":
                                                    Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                                    // 自己也要加入群聊
                                                    set.add(username);
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                                    break;
                                                case "gmembers":
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(username));
                                                    break;
                                                case "gjoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                                    break;
                                                case "gquit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    return;
                                                default:
                                            }

                                        }
                                    }, "system in").start();
                                }

                                // 接收响应消息
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    LOGGER.debug("msg: {}", msg);
                                    if ((msg instanceof LoginResponseMessage)) {
                                        LoginResponseMessage response = (LoginResponseMessage)  msg;
                                        // 登录结果写入
                                        login.set(response.isSuccess());
                                    }

                                    // 下降计数，唤醒system.in现场称
                                    downLatch.countDown();
                                }

                                // 在连接断开时触发
                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    LOGGER.debug("连接已经断开，按任意键退出..");
                                    exit.set(true);
                                }

                                // 在出现异常时触发
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    LOGGER.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                                    exit.set(true);
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 9000)).sync();

            // 此处也等连接建立后发送登录请求

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("client error", e);
        }finally {
            worker.shutdownGracefully();
        }

    }
}
