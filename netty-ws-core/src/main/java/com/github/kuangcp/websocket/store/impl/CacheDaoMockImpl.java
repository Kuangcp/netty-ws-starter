package com.github.kuangcp.websocket.store.impl;

import com.github.kuangcp.websocket.msg.QueueMsg;
import com.github.kuangcp.websocket.store.CacheDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注意 此为演示实现，实际需要外置的Redis等系统做公共存储
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:36
 */
public class CacheDaoMockImpl implements CacheDao {

    private final Map<String, List<QueueMsg>> queueCache = new ConcurrentHashMap<>();
    private final Map<Long, String> userRoute = new ConcurrentHashMap<>();

    @Override
    public void cacheRouteHost(Long userId, String host) {
        userRoute.put(userId, host);
    }

    @Override
    public void deleteRoute(Long userId) {
        userRoute.remove(userId);
    }

    @Override
    public String getRouteHost(Long userId) {
        return userRoute.get(userId);
    }

    @Override
    public Map<Long, String> allUser() {
        return userRoute;
    }

    @Override
    public Set<Long> allUserId() {
        return userRoute.keySet();
    }

    @Override
    public void pushQueueMsg(String host, QueueMsg msg) {
        List<QueueMsg> queue = queueCache.computeIfAbsent(host, v -> new ArrayList<>());
        queue.add(msg);
    }

    @Override
    public List<QueueMsg> pollQueueMsg(String host) {
        return queueCache.get(host);
    }
}
