package com.github.kuangcp.websocket.client;


/**
 * @author <a href="https://github.com/kuangcp">Kuangcp</a>
 * 2024-07-10 10:38
 */
public class ClientTest {
    static final String URL = System.getProperty("url", "ws://127.0.0.1:7094/ws");

    public static void main(String[] args) throws Exception {
        Client.testConnect(URL);
    }
}
