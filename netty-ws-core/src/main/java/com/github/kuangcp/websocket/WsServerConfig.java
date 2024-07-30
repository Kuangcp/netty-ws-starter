package com.github.kuangcp.websocket;

import io.netty.channel.AdaptiveRecvByteBufAllocator;
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

    private String prefix = "/ws";

    private String serverId = "";

    /**
     * HTTP握手请求时最大请求体字符串长度
     */
    private int maxContentLength = 4096;

    /**
     * 单条消息最大字符串字节数，需要按业务设置合理值，防范DOS攻击
     *
     * @see AdaptiveRecvByteBufAllocator.HandleImpl#record(int) 实现扩缩容读写ByteBuf
     */
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
    private int readerIdleThreshold = 2;

    /**
     * 限制建立ws连接需通过认证
     */
    private Boolean connectAuth = false;
}
