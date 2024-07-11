package com.github.kuangcp.ws.starter.config;

import com.github.kuangcp.websocket.WsServerConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:02
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "netty-ws")
@EqualsAndHashCode(callSuper = false)
public class WsNettyServerConfig extends WsServerConfig {
}
