package com.github.kuangcp.websocket.store;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:34
 */
public interface UserDao {

    boolean validUserId(Long userId);

    boolean validUserId(Long userId, String token);

}
