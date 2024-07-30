package com.github.kuangcp.ws.auth;

import com.github.kuangcp.websocket.client.WebSocketClientHandler;
import com.github.kuangcp.websocket.client.WsClient;
import com.github.kuangcp.ws.App;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuangcp
 * 2024-07-30 15:42
 */
@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = App.class)
@AutoConfigureMockMvc
public class AuthTest {

    @Test(expected = WebSocketClientHandshakeException.class)
    public void testAuthFailedViaNoToken() throws Throwable {
        new Thread(() -> App.main(new String[]{"args"})).start();

        // 等待server启动完成
        TimeUnit.SECONDS.sleep(4);
        log.info("start connect");
        String url = "ws://127.0.0.1:5455/ws";
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        WebSocketClientHandshaker shaker = WebSocketClientHandshakerFactory.newHandshaker(
                new URI(url + "?uid=11"), WebSocketVersion.V13, null,
                true, new DefaultHttpHeaders());
        final WebSocketClientHandler handler = new WebSocketClientHandler(shaker, pool) {
            @Override
            public void handleTextMsg(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
                super.handleTextMsg(ctx, frame);
                log.info("MESSAGE: {}", frame.text());
            }
        };

        WsClient client = new WsClient(url, handler);
        client.connect();

        ChannelFuture channelFuture = handler.handshakeFuture();
        Throwable e = channelFuture.cause();
        if (Objects.nonNull(e)) {
            throw e;
        }

        Thread.currentThread().join(20_000);
    }

    @Test
    public void testAuthSuccess() throws Throwable {
        new Thread(() -> App.main(new String[]{"args"})).start();

        // 等待server启动完成
        TimeUnit.SECONDS.sleep(4);
        log.info("start connect");
        String url = "ws://127.0.0.1:5455/ws";
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        WebSocketClientHandshaker shaker = WebSocketClientHandshakerFactory.newHandshaker(
                new URI(url + "?uid=11&token=11"), WebSocketVersion.V13, null,
                true, new DefaultHttpHeaders());
        final WebSocketClientHandler handler = new WebSocketClientHandler(shaker, pool) {
            @Override
            public void handleTextMsg(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
                super.handleTextMsg(ctx, frame);
                log.info("MESSAGE: {}", frame.text());
            }
        };

        WsClient client = new WsClient(url, handler);
        client.connect();

        ChannelFuture channelFuture = handler.handshakeFuture();
        Throwable e = channelFuture.cause();
        if (Objects.nonNull(e)) {
            throw e;
        }

        Thread.currentThread().join(15_000);
        log.info("Finish");
    }
}
