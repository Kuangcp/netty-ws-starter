# netty websocket starter

![](/arch.drawio.svg)

- 支持功能：
  - SDK随应用集成，不单独部署，并且无需考虑类似于Cookie/Session场景下的 session 共享问题
  - 广播消息
  - 心跳检测
  - HTTP握手请求时支持自定义认证
  - 自定义编解码方式，JSON Protobuf等

- 不支持功能：
  - 消息持久化，历史消息搜索 
  - 消息的已读&未读 回执
  - 发送和接收消息的 Once 语义
  - 发送离线消息
  - 断线重连时的节点亲和
  - 节点故障转移时，保证消息不丢失


## Quick Start

[Demo 项目](/netty-ws-server-demo)

> 添加依赖

```xml
<!--添加源-->
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

```xml

<dependency>
    <groupId>com.github.kuangcp</groupId>
    <artifactId>netty-ws-spring-boot-starter</artifactId>
    <version>1.0.5-RELEASE</version>
</dependency>
```

> 基础配置

```yaml
netty-ws:
  # websocket 服务 端口 
  port: 5455
  # ws url 根路径 默认为 ws://host:port/ws
  prefix: /ws
  # HTTP握手请求时最大请求体字符串长度
  max-content-length: 4096
  # 单条消息最大字符串长度，需要按业务设置合理值，防范DOS攻击
  max-frame-size: 65536
  # 60s 未读取到客户端发来的消息 认为是触发一次idle事件
  reader-idle-sec: 60
  # 当idle事件累计到2次后，关闭当前用户连接
  reader-idle-threshold: 2
  # 是否开启 token 认证机制
  connect-auth: true
```

> 自定义连接处理类

```java

@Component
@ChannelHandler.Sharable
public class DemoHandler extends AbstractBizHandler {

    public DemoHandler(CacheDao cacheDao, UserDao userDao, WsServerConfig config) {
        super(cacheDao, userDao, config);
        this.schedulerPollQueueMsg(Executors.newScheduledThreadPool(1));
    }

    @Override
    public void connectSuccess(Long userId) {
        log.info("connected {}", userId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String id = WsSocketUtil.id(ctx);
        Long userId = channelUserMap.get(id);
        log.info("disconnect {}", userId);
        super.channelInactive(ctx);
    }

    @Override
    protected void handSharkHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        super.handSharkHttpRequest(ctx, request);
    }
}
```

> 实现存储扩展 com.github.kuangcp.websocket.store.CacheDao  
> 实现认证扩展 com.github.kuangcp.websocket.store.UserDao  

- 然后启动项目, 验证websocket通信。 可使用 [Js 测试客户端](/netty-ws-server-demo/src/main/resources/client.html) 测试，连接地址 `ws://127.0.0.1:5455/ws?uid={uid}&token={token}`
    - 注意认证的实现方式为HTTP握手时，解析 url参数或者Header中的token值。
    - 如果未开启认证（connect-auth） token可不传

## 设计思路

1. Netty作为通信基础，每个用户连接时通过前置的Nginx等LB层负载均衡到WS集群中的任意节点。
1. 用户和主机ip关系绑定到Redis map结构中 (userid -> host) 
1. 每个主机ip绑定一个Redis的list队列，存放了需要推送的消息（由其他节点写入），解决应用层向用户推送消息时，用户连接随机分散的问题。

注意：Redis可替换成任意中心存储, 已由 CacheDao 抽象, 例如关系型数据库 MySQL，PG；非关系型数据库：MongoDB 等等。

## Dev

mvn clean source:jar deploy -U -DskipTests=true

