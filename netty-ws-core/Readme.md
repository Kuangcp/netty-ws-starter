# TODO

- [X] 业务抽象：认证，重连，踢下线，连接心跳
- [X] 缓存层抽象
- [X] 配置文件化： 端口，最大帧，日志级别
- [X] 案例项目 Redis 接入

包名如下 config.com.github.kuangcp.ws.starter.MainProperties 时报错Bean加载失败

# RoadMap

可靠websocket：

- 多端设备同时在线
- 消息双向ack机制
    - 当消息通过A主机路由转发到B主机时，客户端和B断开了连接，连接到C主机，此时消息又得转移到C主机队列去消费推送。
- 离线消息
- 消息去重

# 同类项目

> [netty-ws-spring-boot-starter](https://github.com/chen-kugua/netty-ws-spring-boot-starter)
