package org.lql.netty.chatandrpc.server.session;

/**
 * Title: SeesionFactory <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:23 <br>
 */
public abstract class SessionFactory {

    private static class SingletonHolder {
        private static final Session SESSION = new SessionMemoryImpl();
    }

    public static Session getSession() {
        return SingletonHolder.SESSION;
    }
}
