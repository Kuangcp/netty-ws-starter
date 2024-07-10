package com.github.kuangcp.websocket.store;

import com.github.kuangcp.websocket.msg.QueueMsg;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:34
 */
public interface CacheDao {

    void cacheRouteHost(Long userId, String host);

    void deleteRoute(Long userId);

    String getRouteHost(Long userId);

    void pushQueueMsg(String host, QueueMsg msg);

    QueueMsg pollQueueMsg(String host);
}