package com.github.kuangcp.websocket.util;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a> 
 * 2024-03-07 10:23
 */
public class WsSocketUtil {


    /**
     * 将路径参数转换成Map对象，如果路径参数出现重复参数名，将以最后的参数值为准
     * @param uri 传入的携带参数的路径
     */
    public static Map<String, String> getParams(String uri) {
        Map<String, String> params = new HashMap<>(10);

        int idx = uri.indexOf("?");
        if (idx != -1) {
            String[] paramsArr = uri.substring(idx + 1).split("&");

            for (String param : paramsArr) {
                idx = param.indexOf("=");
                params.put(param.substring(0, idx), param.substring(idx + 1));
            }
        }

        return params;
    }

    /**
     * 获取URI中参数以外部分路径
     */
    public static String getBasePath(String uri) {
        if (uri == null || uri.isEmpty()) {
            return null;
        }

        int idx = uri.indexOf("?");
        if (idx == -1) {
            return uri;
        }

        return uri.substring(0, idx);
    }

    public static Long parseUserId(String userId) {
        try {
            return Long.parseLong(userId);
        } catch (Exception e) {
            return null;
        }
    }

    public static String id(ChannelHandlerContext ctx) {
        return ctx.channel().id().asShortText();
    }

    public static String remote(ChannelHandlerContext ctx) {
        return ctx.channel().remoteAddress().toString();
    }
}
