package com.github.kuangcp.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a> on 2021-05-18 08:33
 */
@AllArgsConstructor
public class WsChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final WsServer server;

    @Override
    protected void initChannel(SocketChannel ch) {
        WsServerConfig config = server.getConfig();
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("logging", new LoggingHandler(config.getLogLevel()));
        // 优先于业务Handler
        if (config.getReaderIdleSec() > 0) {
            pipeline.addLast("idle", new IdleStateHandler(config.getReaderIdleSec(), 0, 0, TimeUnit.SECONDS));
        }
        pipeline.addLast("http-codec", new HttpServerCodec());//设置解码器
        pipeline.addLast("aggregator", new HttpObjectAggregator(config.getMaxContentLength()));
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());//用于大数据的分区传输
//        pipeline.addLast("handler", new NioWebSocketHandler());//自定义的业务handler
        pipeline.addLast("handler", server.getHandler());//自定义的业务handler

        // checkStartsWith 为true 支持路径带参数
        // maxFrameSize 设置的是最大可申请的ByteBuf，实际上使用时是按需申请和回收内存
        pipeline.addLast("proto", new WebSocketServerProtocolHandler(config.getPrefix(),
                HttpHeaderNames.WEBSOCKET_PROTOCOL.toString(), true, config.getMaxFrameSize(),
                false, true));
    }
}
