package com.github.kuangcp.websocket.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a> 
 * 2024-02-29 14:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueMsg {

    private Long userId;
    private String msg;
}
