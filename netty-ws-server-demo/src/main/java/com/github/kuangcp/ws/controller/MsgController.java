package com.github.kuangcp.ws.controller;

import com.github.kuangcp.websocket.WsMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kuangcp
 * 2024-07-10 19:04
 */
@RestController
@RequestMapping("/msg")
public class MsgController {

    @Autowired
    private WsMsgService msgService;

    @GetMapping("/send")
    public String sendMsg(@RequestParam("userId") Long userId, @RequestParam("msg") String msg) {
        msgService.pushMsg(userId, msg);
        return "OK";
    }
}
