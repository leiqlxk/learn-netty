package org.lql.netty.chat.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: UserServerMemoryImpl <br>
 * ProjectName: learn-netty <br>
 * description: 登录实现类 <br>
 *
 * @author: leiql <br>
 * @version: 1.0 <br>
 * @since: 2021/8/13 22:12 <br>
 */
public class UserServiceMemoryImpl implements UserService{
    private Map<String, String> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("zhangsan", "123");
        allUserMap.put("lisi", "123");
        allUserMap.put("wangwu", "123");
        allUserMap.put("admin", "123");
        allUserMap.put("lql", "123");
    }

    @Override
    public boolean login(String username, String password) {
        String pass = allUserMap.get(username);

        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }
}
