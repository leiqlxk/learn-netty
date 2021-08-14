package org.lql.netty.chatandrpc.server.service;

/**
 * Title: UserService <br>
 * ProjectName: learn-netty <br>
 * description: 用户登录接口 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:10 <br>
 */
public interface UserService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回true，否则返回false
     */
    boolean login(String username, String password);
}
