package org.lql.netty.chat.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lql.netty.chat.message.GroupQuitRequestMessage;
import org.lql.netty.chat.message.GroupQuitResponseMessage;
import org.lql.netty.chat.server.session.Group;
import org.lql.netty.chat.server.session.GroupSessionFactory;

/**
 * Title: GroupQuitRequestMessageHandler <br>
 * ProjectName: learn-netty <br>
 * description: 退出群聊处理 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 0:58 <br>
 */
@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().removeMember(msg.getGroupName(), msg.getUsername());

        if (group != null) {
            ctx.writeAndFlush(new GroupQuitResponseMessage(true, "您已退出[" + msg.getGroupName() + "]聊天组"));
        }else {
            ctx.writeAndFlush(new GroupQuitResponseMessage(false, "不存在[" + msg.getGroupName() + "]聊天组"));
        }
    }
}
