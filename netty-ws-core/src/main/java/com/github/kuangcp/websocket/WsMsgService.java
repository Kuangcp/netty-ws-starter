package com.github.kuangcp.websocket;

/**
 * 消息服务
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 15:05
 */
public interface WsMsgService {

    /**
     * 无需关心用户连接的host，后台转发机制实现
     */
    void pushMsg(Long userId, String txt);

}
