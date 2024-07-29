package com.github.kuangcp.websocket.store;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:34
 */
public interface UserDao {

    /**
     * @param userId 用户id
     * @return true 合法用户 false 非法用户
     */
    boolean validUserId(Long userId);

    /**
     * @param token  token
     * @param userId 用户id
     * @return true 合法用户 false 非法用户
     */
    boolean validUserId(Long userId, String token);

}
