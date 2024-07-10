package com.github.kuangcp.ws.store;

import com.github.kuangcp.websocket.msg.QueueMsg;
import com.github.kuangcp.websocket.store.CacheDao;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:41
 */
@Component
public class CacheDaoImpl implements CacheDao {
    @Override
    public void cacheRouteHost(Long userId, String host) {

    }

    @Override
    public void deleteRoute(Long userId) {

    }

    @Override
    public String getRouteHost(Long userId) {
        return "";
    }

    @Override
    public void pushQueueMsg(String host, QueueMsg msg) {

    }

    @Override
    public QueueMsg pollQueueMsg(String host) {
        return null;
    }
}
