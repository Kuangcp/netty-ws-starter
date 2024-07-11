package com.github.kuangcp.ws.handler;

import com.github.kuangcp.websocket.handler.AbstractBizHandler;
import com.github.kuangcp.websocket.store.CacheDao;
import com.github.kuangcp.websocket.store.UserDao;
import com.github.kuangcp.websocket.util.WsSocketUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:40
 */
@Component
@ChannelHandler.Sharable
public class DemoHandler extends AbstractBizHandler {

    public DemoHandler(CacheDao cacheDao, UserDao userDao) {
        super(cacheDao, userDao);

        this.pollBatch = 500;
        this.schedulerPollQueueMsg(Executors.newScheduledThreadPool(1));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void connectSuccess(Long userId) {
        System.out.println("connected " + userId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String id = WsSocketUtil.id(ctx);
        Long userId = channelUserMap.get(id);
        System.out.println("disconnect " + userId);
        super.channelInactive(ctx);
    }

    @Override
    protected void handSharkHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        super.handSharkHttpRequest(ctx, request);
    }

    @Override
    public boolean needAuth() {
        return true;
    }
}
