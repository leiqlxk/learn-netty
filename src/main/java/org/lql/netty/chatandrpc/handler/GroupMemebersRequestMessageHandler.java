package org.lql.netty.chatandrpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lql.netty.chatandrpc.message.GroupMembersRequestMessage;
import org.lql.netty.chatandrpc.message.GroupMembersResponseMessage;
import org.lql.netty.chatandrpc.server.session.GroupSessionFactory;

import java.util.Set;

/**
 * Title: GroupMemebersRequestMessageHandler <br>
 * ProjectName: learn-netty <br>
 * description: 获取群成员列表处理 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 0:57 <br>
 */
@ChannelHandler.Sharable
public class GroupMemebersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        Set<String> members = GroupSessionFactory.getGroupSession().getMembers(msg.getGroupName());

        ctx.writeAndFlush(new GroupMembersResponseMessage(members));
    }
}
