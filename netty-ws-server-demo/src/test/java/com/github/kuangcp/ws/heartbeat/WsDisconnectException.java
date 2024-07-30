package com.github.kuangcp.ws.heartbeat;

/**
 * @author Kuangcp
 * 2024-07-30 16:18
 */
public class WsDisconnectException extends RuntimeException {
    public WsDisconnectException(String message) {
        super(message);
    }
}
