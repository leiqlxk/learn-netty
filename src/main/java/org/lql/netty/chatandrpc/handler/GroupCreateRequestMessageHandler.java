package org.lql.netty.chatandrpc.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lql.netty.chatandrpc.message.GroupCreateRequestMessage;
import org.lql.netty.chatandrpc.message.GroupCreateResponseMessage;
import org.lql.netty.chatandrpc.server.session.Group;
import org.lql.netty.chatandrpc.server.session.GroupSessionFactory;

import java.util.List;
import java.util.Set;

/**
 * Title: GroupCreateRequestHandler <br>
 * ProjectName: learn-netty <br>
 * description: 创建群处理 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 0:44 <br>
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();

        Group group = GroupSessionFactory.getGroupSession().createGroup(groupName, members);
        // 创建成功
        if (group == null) {
            // 发送群创建成功
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));

            // 发送拉群消息
            List<Channel> channels = GroupSessionFactory.getGroupSession().getMembersChannel(groupName);
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入[" + groupName + "]群聊"));
            }

        }else { // 创建失败
            ctx.channel().writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已存在"));
        }
    }
}
