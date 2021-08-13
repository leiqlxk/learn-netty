package org.lql.netty.chat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lql.netty.chat.message.GroupChatRequestMessage;
import org.lql.netty.chat.message.GroupChatResponseMessage;
import org.lql.netty.chat.server.session.GroupSessionFactory;

import java.util.List;

/**
 * Title: GroupChatRequestMessageHandler <br>
 * ProjectName: learn-netty <br>
 * description: 群聊处理 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 0:55 <br>
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName());

        for (Channel channel : channels) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
            }
        }

        ctx.channel().writeAndFlush(new GroupChatResponseMessage(true, "消息发送成功"));
    }
}
