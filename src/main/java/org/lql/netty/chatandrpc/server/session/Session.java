package org.lql.netty.chatandrpc.server.session;

import io.netty.channel.Channel;

/**
 * Title: Session <br>
 * ProjectName: learn-netty <br>
 * description: 会话管理接口 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:16 <br>
 */
public interface Session {

    /**
     * 绑定会话
     * @param channel 要绑定会话的channel
     * @param username 会话绑定用户
     */
    void bind(Channel channel, String username);

    /**
     * 解绑会话
     * @param channel 要解绑的channel
     */
    void unbind(Channel channel);

    /**
     * 获取属性
     * @param channel  要获取的channel
     * @param name 属性名
     * @return 属性值
     */
    Object getAttribute(Channel channel, String name);

    /**
     * 设置属性
     * @param channel 要设置属性的channel
     * @param name 属性名
     * @param value 属性值
     */
    void  setAttribute(Channel channel, String name, Object value);

    /**
     * 根据用户名获取channel
     * @param username 用户名
     * @return
     */
    Channel getChannel(String username);

    String getUsername(Channel channel);
}
