package com.github.kuangcp.websocket.store;

import com.github.kuangcp.websocket.msg.QueueMsg;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:34
 */
public interface CacheDao {

    void cacheRouteHost(Long userId, String host);

    void deleteRoute(Long userId);

    String getRouteHost(Long userId);

    Map<Long, String> allUser();

    Set<Long> allUserId();

    void pushQueueMsg(String host, QueueMsg msg);

    List<QueueMsg> pollQueueMsg(String host);
}
