package com.github.kuangcp.websocket;

import com.github.kuangcp.websocket.handler.AbstractBizHandler;
import com.github.kuangcp.websocket.handler.SimpleBizHandler;
import com.github.kuangcp.websocket.store.impl.CacheDaoImpl;
import com.github.kuangcp.websocket.store.impl.UserDaoImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

/**
 * 客户端使用 client.html
 * 压测可使用 https://github.com/Kuangcp/GoBase/tree/master/toolbox/web-socket
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a> on 2021-05-18 08:32
 */
@Slf4j
@Getter
public class WsServer {

    private final int port;
    private int maxContentLength = 65535;
    private int maxFrameSize = 65535;
    private String logLevel = "INFO";
    private AbstractBizHandler handler;

    public WsServer(int port) {
        this.port = port;
    }

    public WsServer(int port, String logLevel) {
        this.port = port;
        this.logLevel = logLevel;
    }

    public WsServer(int port, int maxContentLength, int maxFrameSize) {
        this.port = port;
        this.maxContentLength = maxContentLength;
        this.maxFrameSize = maxFrameSize;
    }

    public WsServer(int port, int maxContentLength, int maxFrameSize, String logLevel) {
        this.port = port;
        this.maxContentLength = maxContentLength;
        this.maxFrameSize = maxFrameSize;
        this.logLevel = logLevel;
        this.handler = new SimpleBizHandler(new CacheDaoImpl(), new UserDaoImpl(), Executors.newScheduledThreadPool(1));
    }

    public WsServer(int port, int maxContentLength, int maxFrameSize, String logLevel, AbstractBizHandler handler) {
        this.port = port;
        this.maxContentLength = maxContentLength;
        this.maxFrameSize = maxFrameSize;
        this.logLevel = logLevel;
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
            Channel channel = bootstrap.bind(port).sync().channel();
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

