package com.github.kuangcp.websocket;

import com.github.kuangcp.websocket.handler.AbstractBizHandler;
import com.github.kuangcp.websocket.handler.SingletonBizHandler;
import com.github.kuangcp.websocket.store.impl.CacheDaoMockImpl;
import com.github.kuangcp.websocket.store.impl.UserDaoMockImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端使用 client.html
 * 压测可使用 https://github.com/Kuangcp/GoBase/tree/master/toolbox/web-socket
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a> on 2021-05-18 08:32
 */
@Slf4j
@Getter
public class WsServer {

    private final WsServerConfig config;
    private final AbstractBizHandler handler;

    /**
     * 调试API 单机模式
     */
    public WsServer() {
        this.config = new WsServerConfig();
        this.handler = new SingletonBizHandler(config);
    }

    /**
     * 调试API 单机模式
     */
    public WsServer(int port, String logLevel) {
        this.config = new WsServerConfig().setPort(port).setLogLevel(logLevel);
        this.handler = new SingletonBizHandler(config);
    }

    /**
     * 调试API 单机模式
     */
    public WsServer(WsServerConfig config) {
        this.config = config;
        this.handler = new SingletonBizHandler(config);
    }

    /**
     * 实际应用API
     *
     * @param handler 业务handler
     */
    public WsServer(WsServerConfig config, AbstractBizHandler handler) {
        this.config = config;
        this.handler = handler;
    }

    public void start() {
        log.info("正在启动WebSocket服务器...");
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new WsChannelInitializer(this));
            Channel channel = bootstrap.bind(config.getPort()).sync().channel();
            log.info("WebSocket服务器启动成功：{}", channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("", e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            log.warn("WebSocket服务器已关闭");
        }
    }

}

