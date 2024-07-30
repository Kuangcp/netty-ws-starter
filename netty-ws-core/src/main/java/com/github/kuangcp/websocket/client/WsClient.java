package com.github.kuangcp.websocket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Kuangcp
 * 2024-07-30 15:45
 */
@Slf4j
public class WsClient {
    private EventLoopGroup group;
    private SslContext sslCtx;
    final String host;
    final int port;
    private final WebSocketClientHandler handler;

    public WsClient(String url, WebSocketClientHandler handler) throws URISyntaxException, SSLException {
        this.handler = handler;

        URI uri = new URI(url);
        String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
        host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            System.err.println("Only WS(S) protocol is supported.");
            return;
        }

        final boolean ssl = "wss".equalsIgnoreCase(scheme);
        if (ssl) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

    }

    public void disconnect() {
        group.shutdownGracefully();
    }

    public void connect() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        group = new NioEventLoopGroup(1, pool);
        try {
            clientChannel(handler, group, sslCtx, host, port);
        } catch (Exception e) {
            log.error("", e);
            group.shutdownGracefully();
        }
    }

    public static void clientChannel(WebSocketClientHandler handler, EventLoopGroup group, SslContext sslCtx,
                                     String host, int port) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                        }
                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                WebSocketClientCompressionHandler.INSTANCE,
                                handler);
                    }
                });

        b.connect(host, port).sync().channel();
        handler.handshakeFuture().sync();
    }
}
