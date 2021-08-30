package org.lql.netty.simplechat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Title: ChatServerHanlder <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/7 15:23 <br>
 */
public class ChatServerHanlder extends SimpleChannelInboundHandler<String> {

    // GlobalEventExecutor.INSTANCE是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 表示channel处于就绪状态，提示上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        // 将客户加入聊天的信息推送给其它在线的客户端
        // 该方法会将channelGroup中所有的channel遍历，并发送消息
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 上线了 " + sdf.format(new Date()) + "\n");
        // 将当前 channel加入到channelGroup
        channelGroup.add(channel);
        System.out.println(ctx.channel().remoteAddress() + " 上线了 " + "\n");
    }

    // 表示channel处于不活动状态，提示离线了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        // 将客户端离开信息推送给单签在线的用户
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 下线了  + “\n");
        System.out.println(channel.remoteAddress() + " 下线了 " + "\n");
//        channel.close();
        System.out.println("channelGroup size = " + channelGroup.size());
        ctx.close();

    }

    // 读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 获取当前channel
        Channel channel = ctx.channel();
        // 遍历channelGroup，根据不通更多情况，回不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch) {// 不是当前的channel，转发消息
                ch.writeAndFlush("[客户端]" + channel.remoteAddress() + " 发送了消息：" + msg + "\n");
            }else { // 回显自己的消息
                ch.writeAndFlush("[自己]发送了消息：" + msg + "\n");
            }
        });
        throw new Exception("dasfasdfasdfas");
    }

    // 报错关闭连接
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
