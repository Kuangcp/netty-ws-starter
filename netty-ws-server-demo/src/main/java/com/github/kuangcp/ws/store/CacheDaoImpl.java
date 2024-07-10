package com.github.kuangcp.ws.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kuangcp.websocket.msg.QueueMsg;
import com.github.kuangcp.websocket.store.CacheDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 分布式缓存
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:41
 */
@Component
public class CacheDaoImpl implements CacheDao {

    @Autowired
    private RedisTemplate redisTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String routeKey = "netty-ws-test:router";
    private static final String queueKey = "netty-ws-test:queue";

    @Override
    public void cacheRouteHost(Long userId, String host) {
        redisTemplate.opsForHash().put(routeKey, userId, host);
    }

    @Override
    public void deleteRoute(Long userId) {
        redisTemplate.opsForHash().delete(routeKey, userId);
    }

    @Override
    public String getRouteHost(Long userId) {
        return (String) redisTemplate.opsForHash().get(routeKey, userId);
    }

    @Override
    public void pushQueueMsg(String host, QueueMsg msg) {
        try {
            redisTemplate.opsForList().rightPush(queueKey + ":" + host, mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QueueMsg pollQueueMsg(String host) {
        Object msg = redisTemplate.opsForList().leftPop(queueKey + ":" + host);
        if (Objects.isNull(msg)) {
            return null;
        }
        try {
            return mapper.readValue(msg.toString(), QueueMsg.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
