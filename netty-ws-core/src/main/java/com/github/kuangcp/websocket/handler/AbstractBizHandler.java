package com.github.kuangcp.websocket.handler;

import com.github.kuangcp.websocket.WsMsgService;
import com.github.kuangcp.websocket.WsServerConfig;
import com.github.kuangcp.websocket.constants.Const;
import com.github.kuangcp.websocket.msg.QueueMsg;
import com.github.kuangcp.websocket.store.CacheDao;
import com.github.kuangcp.websocket.store.UserDao;
import com.github.kuangcp.websocket.util.IpUtils;
import com.github.kuangcp.websocket.util.WsSocketUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 14:15
 */
@Slf4j
public abstract class AbstractBizHandler extends SimpleChannelInboundHandler<WebSocketFrame> implements WsMsgService {

    protected static final Map<Long, Channel> userMap = new ConcurrentHashMap<>();
    protected static final Map<String, Long> channelUserMap = new ConcurrentHashMap<>();
    private static final Map<String, AtomicInteger> idleMap = new ConcurrentHashMap<>();

    final WsServerConfig config;
    final CacheDao cacheDao;
    final UserDao userDao;

    public AbstractBizHandler(CacheDao cacheDao, UserDao userDao, WsServerConfig config) {
        this.cacheDao = cacheDao;
        this.userDao = userDao;
        this.config = config;
    }

    public abstract void connectSuccess(Long userId);

    @Override
    public void pushMsg(Long userId, String txt) {
        try {
            Channel channel = userMap.get(userId);
            if (Objects.isNull(channel)) {
                String host = cacheDao.getRouteHost(userId);
                if (Objects.nonNull(host)) {
                    cacheDao.pushQueueMsg(host, new QueueMsg(userId, txt));
                }
                return;
            }
            log.debug("PUSH: userId={} {}", userId, txt);
            channel.writeAndFlush(new TextWebSocketFrame(txt));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void pushTxtMsg(Long userId, String msg) {
        try {
            Channel channel = userMap.get(userId);
            if (Objects.isNull(channel)) {
                return;
            }
            log.debug("PUSH: userId={} {}", userId, msg);
            channel.writeAndFlush(new TextWebSocketFrame(msg));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    protected void schedulerPollQueueMsg(ScheduledExecutorService scheduler) {
        // 定时消费 需要推送的消息
        String hostIp = IpUtils.getHostIp();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<QueueMsg> msgList = cacheDao.pollQueueMsg(hostIp);
                if (Objects.isNull(msgList) || msgList.isEmpty()) {
                    return;
                }
                for (QueueMsg msg : msgList) {
                    log.debug("read: userId={}", msg.getUserId());
                    pushTxtMsg(msg.getUserId(), msg.getMsg());
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }, 10, 1, TimeUnit.SECONDS);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Conn {} host:{} size: {} {}", WsSocketUtil.id(ctx), WsSocketUtil.remote(ctx), userMap.size(), channelUserMap.size());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String id = WsSocketUtil.id(ctx);
        Long userId = channelUserMap.get(id);
        if (Objects.nonNull(userId)) {
            log.debug("DisConn {} {} userId:{} size:{} {}", id, userId, WsSocketUtil.remote(ctx), userMap.size(), channelUserMap.size());
            userMap.remove(userId);
            cacheDao.deleteRoute(userId);
            channelUserMap.remove(id);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 调用此方法才会进入 IdleStateHandler 的 channelReadComplete 方法
        ctx.fireChannelReadComplete();
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     */
    static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 唯一的一次http请求，用于升级至websocket 需要正确响应
     *
     * @throws WebSocketHandshakeException 终止握手流程
     */
    protected void handSharkHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        Map<String, String> params = WsSocketUtil.getParams(uri);
//        log.info("客户端请求参数：{}", params);

        String userIdStr = params.get("uid");
        Long userId = WsSocketUtil.parseUserId(userIdStr);
        if (Objects.isNull(userId)) {
            throw new WebSocketHandshakeException("no auth");
        }

        if (BooleanUtils.isTrue(config.getConnectAuth())) {
            String token = params.get("token");
            if (StringUtils.isBlank(token)) {
                HttpHeaders headers = request.trailingHeaders();
                token = headers.get("token");
            }
            if (StringUtils.isBlank(token)) {
                log.warn("no auth userId={}", userId);
                throw new WebSocketHandshakeException("no auth");
            }

            if (!userDao.validUserId(userId, token)) {
                log.warn("invalid user: userId={} token={}", userId, token);
                throw new WebSocketHandshakeException("no auth");
            }
        } else {
            if (!userDao.validUserId(userId)) {
                log.warn("invalid user: userId={}", userId);
                throw new WebSocketHandshakeException("no auth");
            }
        }

        // 关闭原有连接
        if (userMap.containsKey(userId)) {
            Channel last = userMap.get(userId);
            if (!Objects.equals(last, ctx.channel())) {
                last.writeAndFlush(new TextWebSocketFrame("reset"));
                log.warn("close last  user:{} host:{}", userId, WsSocketUtil.remote(ctx));
                last.close();
                userMap.remove(userId);
                channelUserMap.remove(last.id().asShortText());
            }
        }

        String hostIp = IpUtils.getHostIp();
        cacheDao.cacheRouteHost(userId, hostIp);

        userMap.put(userId, ctx.channel());
        channelUserMap.put(WsSocketUtil.id(ctx), userId);
        connectSuccess(userId);

        // 判断请求路径是否跟配置中的一致
        if (Const.webSocketPath.equals(WsSocketUtil.getBasePath(uri))) {
            // 因为有可能携带了参数，导致客户端一直无法返回握手包，因此在校验通过后，重置请求路径
            request.setUri(Const.webSocketPath);
        } else {
            throw new WebSocketHandshakeException("invalid");
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof PingWebSocketFrame) {
            pingWebSocketFrameHandler(ctx, (PingWebSocketFrame) frame);
        } else if (frame instanceof TextWebSocketFrame) {
            textWebSocketFrameHandler(ctx, (TextWebSocketFrame) frame);
        } else if (frame instanceof CloseWebSocketFrame) {
            closeWebSocketFrameHandler(ctx, (CloseWebSocketFrame) frame);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("客户端请求数据类型：{}", msg.getClass());
        if (msg instanceof FullHttpRequest) {
            handSharkHttpRequest(ctx, (FullHttpRequest) msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                String id = WsSocketUtil.id(ctx);
                AtomicInteger cnt = idleMap.computeIfAbsent(id, v -> new AtomicInteger(0));
                if (cnt.incrementAndGet() >= config.getReaderIdleThreshold()) {
                    log.warn("close idle channel: id={}", id);
                    idleMap.remove(id);
                    ctx.channel().close();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 客户端发送断开请求处理
     */
    void closeWebSocketFrameHandler(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
        ctx.close();
    }

    /**
     * 创建连接之后，客户端发送的消息都会在这里处理
     */
    void textWebSocketFrameHandler(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        ctx.channel().writeAndFlush(frame.retain());
    }

    /**
     * 处理客户端心跳包
     */
    void pingWebSocketFrameHandler(ChannelHandlerContext ctx, PingWebSocketFrame frame) {
        ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
    }
}
