package org.lql.netty.chat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lql.netty.chat.message.GroupJoinRequestMessage;
import org.lql.netty.chat.message.GroupJoinResponseMessage;
import org.lql.netty.chat.server.session.Group;
import org.lql.netty.chat.server.session.GroupSessionFactory;

import java.util.List;

/**
 * Title: GroupJionRequestHandler <br>
 * ProjectName: learn-netty <br>
 * description: 加入群聊处理 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 0:53 <br>
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().joinMember(msg.getGroupName(), msg.getUsername());

        if (group != null) {
            ctx.channel().writeAndFlush(new GroupJoinResponseMessage(true, "加入[" + msg.getGroupName() +"]聊天组成功"));
        }else {
            ctx.channel().writeAndFlush(new GroupJoinResponseMessage(false, "加入[" + msg.getGroupName() +"]聊天组成失败"));
        }
    }
}
