package com.github.kuangcp.websocket.handler;


import com.github.kuangcp.websocket.WsServerConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 单机模式
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a> on 2021-05-18 08:33
 */
@Slf4j
@ChannelHandler.Sharable
public class SingletonBizHandler extends AbstractBizHandler {
    public SingletonBizHandler(WsServerConfig config) {
        super(config);

//        this.schedulerPollQueueMsg(scheduler);
    }

    @Override
    public void closeWebSocketFrameHandler(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
        log.info("close connection");
        super.closeWebSocketFrameHandler(ctx, frame);
    }


    @Override
    public void textWebSocketFrameHandler(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String text = frame.text();
        if (Objects.equals(text, "ping")) {
            log.info("text ping");
            ctx.channel().writeAndFlush(new TextWebSocketFrame("pong"));
            return;
        }
        log.info(text);
    }

    @Override
    public void pingWebSocketFrameHandler(ChannelHandlerContext ctx, PingWebSocketFrame frame) {
        log.info("ping");
        super.pingWebSocketFrameHandler(ctx, frame);
    }

    @Override
    protected void handSharkHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        super.handSharkHttpRequest(ctx, request);
    }


    @Override
    public void connectSuccess(Long userId) {
        log.info("online userId={}", userId);
    }

}
