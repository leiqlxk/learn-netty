package org.lql.netty.chat.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

/**
 * Title: GroupSession <br>
 * ProjectName: learn-netty <br>
 * description: 聊天组会话管理接口 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:30 <br>
 */
public interface GroupSession {

    /**
     * 创建一个聊天组，如果不存在才能创建成功，否则返回null
     * @param name 组名
     * @param members 成员
     * @return 成功时返回组对象，失败返回null
     */
    Group createGroup(String name, Set<String> members);

    /**
     * 加入聊天组
     * @param name 组名
     * @param member 成员名
     * @return 如果组不存在返回null，否则返回组对象
     */
    Group joinMember(String name, String member);

    /**
     * 移除聊天组
     * @param name
     * @return
     */
    Group removeGroup(String name);

    /**
     * 移除聊天组成员
     * @param name 组名
     * @param member 组员
     * @return 如果组不存在返回null，否则返回组对象
     */
    Group removeMember(String name, String member);

    /**
     * 获取组成员
     * @param name 组名
     * @return 成员集合，没有成员会返回 empty set
     */
    Set<String> getMembers(String name);

    /**
     * 获取组成员的channel集合，有在线的channel才会返回
     * @param name 组名
     * @return 成员 channel 集合
     */
    List<Channel> getMembersChannel(String name);
}
