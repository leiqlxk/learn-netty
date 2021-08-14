package org.lql.netty.chatandrpc.server.service;

/**
 * Title: UserServiceFactory <br>
 * ProjectName: learn-netty <br>
 * description: TODO <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:15 <br>
 */
public abstract class UserServiceFactory {

    private static class SingletonHolder {

        private static final UserService USER_SERVICE = new UserServiceMemoryImpl();
    }

    public static UserService getUserService() {
        return SingletonHolder.USER_SERVICE;
    }
}
