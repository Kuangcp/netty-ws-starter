package com.github.kuangcp.ws.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kuangcp
 * 2024-08-30 19:01
 */
@Slf4j
@RestController
@RequestMapping("/lock")
public class LockTestController {

    public static final String Judge = "local cnt = redis.call('incr', KEYS[1]);" +
            "  if (tonumber(cnt) > tonumber(ARGV[1]) ) then redis.call('decr', KEYS[1]); return 0;" +
            " else return 1; end";

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/acquire")
    public boolean acquire() {
        // 指定 lua 脚本，并且指定返回值类型
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(Judge, Integer.class);
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Object lockB = redisTemplate.execute(redisScript, Collections.singletonList("lockB"), 3);
        if (Objects.isNull(lockB)) {
            return false;
        }
        return Integer.parseInt(lockB.toString()) > 0;
    }


    @GetMapping("/release")
    public String release() {
        Long val = redisTemplate.opsForValue().decrement("lockB");
        return val + "";
    }

    @GetMapping("/bench")
    public String benchRun() {
        ScheduledExecutorService sch = Executors.newScheduledThreadPool(1);

        AtomicInteger cnt = new AtomicInteger();
        sch.scheduleAtFixedRate(() -> {
            log.info(" cnt={}", cnt.get());
        }, 3, 500, TimeUnit.MILLISECONDS);

        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            pool.execute(() -> {
                try {
                    while (!acquire()) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (Exception e) {
                            log.error("", e);
                        }
                    }

                    cnt.incrementAndGet();
                    int ms = ThreadLocalRandom.current().nextInt(10000) + 2000;
                    log.info("start run {}", finalI);
                    try {
                        TimeUnit.MILLISECONDS.sleep(ms);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    log.info("finish run {}", finalI);
                    cnt.decrementAndGet();
                    release();
                }
            });
        }

        return "";
    }
}
