package com.github.kuangcp.websocket.starter;

import com.github.kuangcp.websocket.handler.AbstractBizHandler;
import com.github.kuangcp.websocket.WsServer;
import com.github.kuangcp.websocket.starter.config.MainProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 10:44
 */
@Slf4j
@Configuration
public class WsAutoConfiguration {

    @Autowired
    private MainProperties conf;

    @Autowired(required = false)
    private AbstractBizHandler handler;

    @Bean
    @ConditionalOnMissingBean(name = "wsServer")
    public WsServer wsServer() {
        WsServer wsServer;
        if (Objects.nonNull(handler)) {
            wsServer = new WsServer(conf.getPort(), conf.getMaxContentLength(),
                    conf.getMaxFrameSize(), conf.getLogLevel(), handler);
        } else {
            wsServer = new WsServer(conf.getPort(), conf.getMaxContentLength(),
                    conf.getMaxFrameSize(), conf.getLogLevel());
        }

        Thread thread = new Thread(wsServer::start);
        thread.setName("ws-server");
        thread.setDaemon(true);
        thread.start();
        return wsServer;
    }
}
