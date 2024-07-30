package com.github.kuangcp.ws.heartbeat;

import com.github.kuangcp.websocket.client.WebSocketClientHandler;
import com.github.kuangcp.websocket.client.WsClient;
import com.github.kuangcp.ws.App;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Kuangcp
 * 2024-07-30 16:11
 */
@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = App.class)
@AutoConfigureMockMvc
public class HeartBeatTest {

    @Test(expected = WsDisconnectException.class)
    public void testAuthSuccessWithNoHeart() throws Throwable {
        new Thread(() -> App.main(new String[]{"args"})).start();

        // 等待server启动完成
        TimeUnit.SECONDS.sleep(4);
        log.info("start connect");
        String url = "ws://127.0.0.1:5455/ws";
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        WebSocketClientHandshaker shaker = WebSocketClientHandshakerFactory.newHandshaker(
                new URI(url + "?uid=11&token=11"), WebSocketVersion.V13, null,
                true, new DefaultHttpHeaders());

        AtomicReference<Throwable> ex = new AtomicReference<>();
        final WebSocketClientHandler handler = new WebSocketClientHandler(shaker, pool) {

            @Override
            public void handSharkPost(ChannelHandlerContext ctx) {
            }

            @Override
            public void handleTextMsg(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
                super.handleTextMsg(ctx, frame);
                log.info("Client Receive: {}", frame.text());
            }


            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                throw new WsDisconnectException("Disconnect");
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                super.exceptionCaught(ctx, cause);
                ex.set(cause);
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
        if (Objects.nonNull(ex.get())) {
            throw ex.get();
        }

        log.info("Finish");
    }

    @Test
    public void testAuthSuccessKeepHeart() throws Throwable {
        new Thread(() -> App.main(new String[]{"args"})).start();

        // 等待server启动完成
        TimeUnit.SECONDS.sleep(4);
        log.info("start connect");
        String url = "ws://127.0.0.1:5455/ws";
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        WebSocketClientHandshaker shaker = WebSocketClientHandshakerFactory.newHandshaker(
                new URI(url + "?uid=11&token=11"), WebSocketVersion.V13, null,
                true, new DefaultHttpHeaders());

        AtomicReference<Throwable> ex = new AtomicReference<>();
        final WebSocketClientHandler handler = new WebSocketClientHandler(shaker, pool) {

            @Override
            public void handSharkPost(ChannelHandlerContext ctx) {
                pool.scheduleAtFixedRate(() -> {
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("ping"));
                }, 5, 1, TimeUnit.SECONDS);
            }

            @Override
            public void handleTextMsg(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
                super.handleTextMsg(ctx, frame);
                log.info("Client Receive: {}", frame.text());
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                throw new WsDisconnectException("Disconnect");
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                super.exceptionCaught(ctx, cause);
                ex.set(cause);
            }
        };

        WsClient client = new WsClient(url, handler);
        client.connect();

        ChannelFuture channelFuture = handler.handshakeFuture();
        Throwable e = channelFuture.cause();
        if (Objects.nonNull(e)) {
            throw e;
        }

        Thread.currentThread().join(30_000);
        if (Objects.nonNull(ex.get())) {
            throw ex.get();
        }

        log.info("Finish");
    }
}
