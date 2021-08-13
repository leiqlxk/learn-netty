package org.lql.netty.chat.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lql.netty.chat.server.service.UserServiceFactory;
import org.lql.netty.chat.server.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: QuitHandler <br>
 * ProjectName: learn-netty <br>
 * description: 退出处理 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/14 1:37 <br>
 */
public class QuitHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuitHandler.class);

    // 当连接断开触发事件，正常断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String username = SessionFactory.getSession().getUsername(ctx.channel());
        SessionFactory.getSession().unbind(ctx.channel());
        LOGGER.debug("{} 已断开", username);
    }

    // 异常断开
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String username = SessionFactory.getSession().getUsername(ctx.channel());
        SessionFactory.getSession().unbind(ctx.channel());

        LOGGER.debug("{} 已异常断开 异常是 {}", username, cause);
    }
}
