package org.lql.netty.chat.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lql.netty.chat.message.LoginRequestMessage;
import org.lql.netty.chat.message.LoginResponseMessage;
import org.lql.netty.chat.server.service.UserServiceFactory;
import org.lql.netty.chat.server.session.SessionFactory;

/**
 * Title: LoginRequestMessageHandler <br>
 * ProjectName: learn-netty <br>
 * description: 登录处理 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 0:32 <br>
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();

        boolean login = UserServiceFactory.getUserService().login(username, password);

        LoginResponseMessage message;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);

            message = new LoginResponseMessage(true, "登录成功");
        } else {
            message = new LoginResponseMessage(false, "用户名或密码不正常");
        }

        ctx.writeAndFlush(message);
    }
}
