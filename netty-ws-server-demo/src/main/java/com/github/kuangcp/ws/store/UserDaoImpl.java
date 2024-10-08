package com.github.kuangcp.ws.store;

import com.github.kuangcp.websocket.store.UserDao;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 认证
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:41
 */
@Component
public class UserDaoImpl implements UserDao {
    @Override
    public boolean validUserId(Long userId) {
        //todo query db to validate
        return true;
    }

    @Override
    public boolean validUserId(Long userId, String token) {
        //todo query db and parse token
        return Objects.equals(userId + "", token);
    }
}
