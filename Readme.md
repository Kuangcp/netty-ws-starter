# netty websocket starter

## Quick Start
[Demo 项目](/netty-ws-server-demo)

> 添加依赖

```xml
    <dependency>
        <groupId>com.github.kuangcp</groupId>
        <artifactId>netty-ws-spring-boot-starter</artifactId>
        <version>x.x.x</version>
    </dependency>
```

> 基础配置

```yaml
netty-ws:
  port: 5455
  max-content-length: 4096
  max-frame-size: 65536

```

> 自定义连接处理类
```java
@Component
@ChannelHandler.Sharable
public class DemoHandler extends AbstractBizHandler {

    public DemoHandler(CacheDao cacheDao, UserDao userDao) {
        super(cacheDao, userDao);
        
        this.schedulerPollQueueMsg(Executors.newScheduledThreadPool(1));
    }

    @Override
    public void connectSuccess(Long userId) {
        System.out.println("connected " + userId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String id = WsSocketUtil.id(ctx);
        Long userId = channelUserMap.get(id);
        System.out.println("disconnect " + userId);
        super.channelInactive(ctx);
    }

    @Override
    protected void handSharkHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        super.handSharkHttpRequest(ctx, request);
    }

    @Override
    public boolean needAuth() {
        return true;
    }
}
```
> 实现存储扩展 com.github.kuangcp.websocket.store.CacheDao  
> 实现认证扩展 com.github.kuangcp.websocket.store.UserDao  

- 然后启动项目, 验证websocket通信。 可使用 [Js 测试客户端](/netty-ws-server-demo/src/main/resources/client.html) 测试
    - 连接地址 `ws://127.0.0.1:5455/ws?uid={uid}&token={token}`
    - 注意认证的实现方式为HTTP握手时，将Token作为url参数或者作为Header，参数名都是token


## 设计思路
Netty作为通信基础，每个用户连接时通过前置的Nginx等SLB层负载均衡到WS集群。

1. 用户和主机ip关系绑定到Redis map结构中
1. 每个主机ip绑定一个Redis的list队列，存放了其他节点写入的消息数据，解决应用层向用户推送消息时，用户连接随机分散的问题。

注意：Redis可替换成任意中心存储, 已由 CacheDao 抽象，应用层自己实现。
