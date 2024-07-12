package com.github.kuangcp.ws.handler;

import com.github.kuangcp.websocket.WsServerConfig;
import com.github.kuangcp.websocket.handler.AbstractBizHandler;
import com.github.kuangcp.websocket.store.CacheDao;
import com.github.kuangcp.websocket.store.UserDao;
import com.github.kuangcp.websocket.util.WsSocketUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:40
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class DemoHandler extends AbstractBizHandler {

    public DemoHandler(CacheDao cacheDao, UserDao userDao, WsServerConfig config) {
        super(cacheDao, userDao, config);

        this.schedulerPollQueueMsg(Executors.newScheduledThreadPool(1));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void connectSuccess(Long userId) {
        log.info("connected {}", userId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String id = WsSocketUtil.id(ctx);
        Long userId = channelUserMap.get(id);
        log.info("disconnect {}", userId);
        super.channelInactive(ctx);
    }

    @Override
    protected void handSharkHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        super.handSharkHttpRequest(ctx, request);
    }

    @Override
    public void textWebSocketFrameHandler(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        super.textWebSocketFrameHandler(ctx, frame);
    }
}
