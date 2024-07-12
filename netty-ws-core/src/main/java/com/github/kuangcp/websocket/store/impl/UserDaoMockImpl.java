package com.github.kuangcp.websocket.store.impl;

import com.github.kuangcp.websocket.store.UserDao;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:36
 */
public class UserDaoMockImpl implements UserDao {
    @Override
    public boolean validUserId(Long userId) {
        return true;
    }

    @Override
    public boolean validUserId(Long userId, String token) {
        return true;
    }
}
