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

    /**
     * @param userId 用户id
     * @param host   主机标识符
     */
    void cacheRouteHost(Long userId, String host);

    /**
     * @param userId 用户id
     */
    void deleteRoute(Long userId);

    /**
     * @param userId 用户id
     */
    String getRouteHost(Long userId);

    /**
     * 所有在线的用户
     *
     * @return userId -> host
     */
    Map<Long, String> allUser();

    /**
     * 所有在线的用户
     *
     * @return userId
     */
    Set<Long> allUserId();

    /**
     * 从当前主机推送消息到目标主机上的连接用户
     *
     * @param host 目标主机
     * @param msg  消息内容
     */
    void pushQueueMsg(String host, QueueMsg msg);

    /**
     * 消费当前主机的待发送队列消息
     *
     * @param host 主机标识符
     * @return 消息
     */
    List<QueueMsg> pollQueueMsg(String host);
}
