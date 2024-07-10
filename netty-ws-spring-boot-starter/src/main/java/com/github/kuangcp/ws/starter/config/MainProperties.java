package com.github.kuangcp.ws.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:02
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "netty-ws")
public class MainProperties {

    private Integer port;
    private Integer maxContentLength = 65535;
    private Integer maxFrameSize = 65535;
    private String logLevel = "INFO";

}
