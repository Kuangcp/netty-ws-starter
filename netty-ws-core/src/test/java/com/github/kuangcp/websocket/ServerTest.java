package com.github.kuangcp.websocket;


/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 10:37
 */
public class ServerTest {

    public static void main(String[] args) {
        new WsServer(7094, "DEBUG").start();
    }
}
