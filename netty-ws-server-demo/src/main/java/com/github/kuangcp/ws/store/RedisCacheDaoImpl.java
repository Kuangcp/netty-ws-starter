package com.github.kuangcp.ws.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kuangcp.websocket.msg.QueueMsg;
import com.github.kuangcp.websocket.store.CacheDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 分布式缓存
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:41
 */
@Component
public class RedisCacheDaoImpl implements CacheDao {

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
    public Map<Long, String> allUser() {
        return redisTemplate.opsForHash().entries(routeKey);
    }

    @Override
    public Set<Long> allUserId() {
        return redisTemplate.opsForHash().keys(routeKey);
    }

    @Override
    public void pushQueueMsg(String host, QueueMsg msg) {
        try {
            redisTemplate.opsForList().rightPush(getHostKey(host), mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getHostKey(String host) {
        return queueKey + ":" + host;
    }

    @Override
    public List<QueueMsg> pollQueueMsg(String host) {
        String hostKey = getHostKey(host);
        List list = redisTemplate.opsForList().range(hostKey, 0, 100);
        if (Objects.isNull(list) || list.isEmpty()) {
            return Collections.emptyList();
        }

        redisTemplate.opsForList().trim(hostKey, list.size(), Integer.MAX_VALUE);
        List<QueueMsg> result = new ArrayList<>(list.size());
        for (Object o : list) {
            try {
                result.add(mapper.readValue(o.toString(), QueueMsg.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private QueueMsg popMsg(String host) {
        Object msg = redisTemplate.opsForList().leftPop(getHostKey(host));
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
