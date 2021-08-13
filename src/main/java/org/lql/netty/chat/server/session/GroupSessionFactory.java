package org.lql.netty.chat.server.session;

/**
 * Title: GroupSessionFactory <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:48 <br>
 */
public class GroupSessionFactory {

    private static class SingletonHolder {
        private static final GroupSession GROUP_SESSION = new GroupSessionMemoryImpl();
    }

    public static GroupSession getGroupSession() {
        return SingletonHolder.GROUP_SESSION;
    }
}
