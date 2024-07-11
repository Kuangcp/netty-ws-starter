package com.github.kuangcp.websocket;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Kuangcp
 * 2024-07-11 17:20
 */
@Data
@Accessors(chain = true)
public class WsServerConfig {

    private int port = 7094;
    private int maxContentLength = 65535;
    private int maxFrameSize = 65535;
    private String logLevel = "INFO";

    /**
     * 60s 未读取到客户端消息 认为是触发一次idle事件
     */
    private int readerIdleSec = 60;

    /**
     * 当idle事件累计到2次后，关闭连接
     * <p>
     * 即：客户端2min未发送消息到服务端
     */
    private int readerIdleCnt = 2;
}
