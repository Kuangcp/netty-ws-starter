# websocket netty

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
        super(cacheDao, userDao, Executors.newScheduledThreadPool(1));
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

然后启动项目使用客户端（js/java） [Js 测试客户端](/netty-ws-server-demo/src/main/resources/client.html)

连接地址 ws://127.0.0.1:5455/ws?uid={uid}&token={token} 验证通信

注意认证方式为HTTP握手时将Token传入url参数或者作为 HTTP Header，参数名都是token


