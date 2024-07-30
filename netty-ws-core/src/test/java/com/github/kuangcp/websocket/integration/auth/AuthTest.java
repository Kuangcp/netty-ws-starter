package com.github.kuangcp.websocket.integration.auth;

import com.github.kuangcp.websocket.WsServer;
import com.github.kuangcp.websocket.client.BenchmarkClient;

/**
 * @author Kuangcp
 * 2024-07-30 15:20
 */
public class AuthTest {

    public void testAuthFailed() throws Exception {

        new Thread(() -> new WsServer(7094, "DEBUG").start()).start();

        BenchmarkClient.testConnect("ws://127.0.0.1:7094/ws", 1);


    }

}
