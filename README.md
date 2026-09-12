# enterprise-finance-parent

Spring Boot 3.2 + Spring Cloud 2023.0.x 微服务骨架，实现金融级分布式系统和分布式数据一致性。包含 Nacos、Nacos Config、LoadBalancer、OpenFeign、Sentinel、Gateway、Seata。

## 环境要求

本机 `JAVA_HOME` 必须指向 JDK 17（Spring Boot 3.2 不支持 JDK 8）。若系统默认仍是 JDK 8，可临时指定：

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## 版本

| 组件 | 版本 |
| --- | --- |
| JDK | 17 |
| Spring Boot | 3.2.12 |
| Spring Cloud | 2023.0.3 |
| Spring Cloud Alibaba | 2023.0.3.4 |
| Nacos Server | 2.3.2 |
| Seata Server | 2.0.0 |
| Sentinel Dashboard | 1.8.6 |
| MyBatis-Plus | 3.5.7 |

## 模块

| 模块 | 端口 | 说明 |
| --- | --- | --- |
| enterprise-finance-gateway | 8080 | 网关，`/user/**`、`/order/**`、`/product/**` |
| enterprise-finance-user | 8081 | 用户服务，库 `user` |
| enterprise-finance-order | 8082 | 订单服务，库 `order` |
| enterprise-finance-product | 8083 | 商品服务，库 `product` |
| enterprise-finance-common | - | 横切公共：`R`、分页、异常、Demo 配置 |
| enterprise-finance-user-api | - | 用户契约：`UserApi` + DTO + Feign |
| enterprise-finance-order-api | - | 订单契约：`OrderApi` + DTO + Feign |
| enterprise-finance-product-api | - | 商品契约：`ProductApi` + DTO + Feign |

依赖约定：

- `common`：仅横切能力，不含业务 Feign/DTO
- 业务服务依赖自身 `*-api`（本服务契约）+ 调用方需要的其他 `*-api`
- 服务间接口定义在 `XxxApi`，消费方 `XxxFeignClient extends XxxApi`，提供方 `XxxInternalController implements XxxApi`。改契约时两边编译期一起变
- App / 演示接口仍在 `XxxController`（如 `/user/orders`、`/product/purchase`、Sentinel）
- 示例：`user` 依赖 `user-api` + `order-api`；`order` 依赖 `order-api` + `user-api`；`product` 依赖三者 api
- `@EnableFeignClients` 只扫描**需要调用**的 api Feign 包，避免全量启用

| 服务 | 启用 Feign 包 |
| --- | --- |
| user | `com.enterprise.finance.order.api.feign` |
| order | `com.enterprise.finance.user.api.feign` |
| product | `user.api.feign` + `order.api.feign` |

中间件由 `deploy/docker-compose.yml` 启动（本地已有 MySQL，不再容器化）：

| 服务 | 地址 | 账号 |
| --- | --- | --- |
| MySQL | localhost:3306 | root / 123456 |
| Nacos | http://localhost:8848/nacos | nacos / nacos |
| Seata | 8091（控制台 7091） | 注册到 Nacos `SEATA_GROUP` |
| Sentinel | http://localhost:18858 | sentinel / sentinel |

## 启动顺序

1. 确认本机 MySQL 可连：`localhost:3306` / `root` / `123456`。
2. 初始化数据库：

```powershell
cd enterprise-finance-parent
.\deploy\init-mysql.ps1
```

或手动执行 `deploy/sql` 下 `01` 到 `05` 的 SQL。

3. 启动中间件：

> **⚠️ 注意：修改 Seata 注册 IP**
> 在启动之前，请打开 `deploy/docker-compose.yml` 文件，将 `seata-server` 配置中的 `SEATA_IP` 和 `JAVA_OPTS` 里的 IP 地址（目前配置为 `192.168.0.110`，这是某个开发者的本机 IPv4 地址）修改为你自己本机的物理局域网 IPv4 地址（可以通过 `ipconfig` 命令查看，建议使用“无线局域网适配器 WLAN”或“以太网适配器”下的 IPv4 地址）。**不要使用 `127.0.0.1`**，否则本地 IDE 中的微服务将无法连接到 Docker 内部的 Seata 服务。

```powershell
docker compose -f deploy/docker-compose.yml up -d
```

`deploy/docker-compose.yml` 使用国内镜像加速（避免直连 Docker Hub 出现 `not found` / `denied`）：

- Nacos：`docker.m.daocloud.io/nacos/nacos-server:v2.3.2`（DaoCloud 白名单内）
- Seata：`docker.1ms.run/seataio/seata-server:2.0.0`（官方 `apache/seata-server` 无 `2.0.0` 标签；DaoCloud 未收录 `seataio`）
- Sentinel：`docker.1ms.run/bladex/sentinel-dashboard:1.8.6`（DaoCloud 未收录 `bladex`）

若某个镜像仍拉取失败，可单独 `docker pull` 对应地址排查，或把前缀换成 `docker.1panel.live/` 后重新 `up -d`。

Windows 若报 `8858 bind forbidden`，说明 Hyper-V 保留了该端口；本项目已将 Sentinel 宿主机端口改为 **18858**（容器内仍是 8858）。先清理旧容器再启动：

```powershell
docker compose -f deploy/docker-compose.yml down
docker compose -f deploy/docker-compose.yml up -d
```

Sentinel 控制台访问地址：http://localhost:18858

4. 导入 Nacos 配置（等 Nacos 健康后再执行）：

```powershell
.\deploy\nacos\import.ps1
```

5. 编译并启动业务服务：

```powershell
mvn -DskipTests clean package
```

分别启动：

- `enterprise-finance-gateway`
- `enterprise-finance-user`
- `enterprise-finance-order`
- `enterprise-finance-product`

IDE 运行各模块 `*Application`，或：

```powershell
java -jar enterprise-finance-gateway\target\enterprise-finance-gateway-1.0.0-SNAPSHOT.jar
java -jar enterprise-finance-user\target\enterprise-finance-user-1.0.0-SNAPSHOT.jar
java -jar enterprise-finance-order\target\enterprise-finance-order-1.0.0-SNAPSHOT.jar
java -jar enterprise-finance-product\target\enterprise-finance-product-1.0.0-SNAPSHOT.jar
```

启动后在 Nacos 服务列表应看到 `enterprise-finance-gateway`、`enterprise-finance-user`、`enterprise-finance-order`、`enterprise-finance-product`、`seata-server`。

## 验收接口

以下请求都走网关 `http://localhost:8080`。

### 配置中心

```bash
curl http://localhost:8080/user/config
curl http://localhost:8080/order/config
curl http://localhost:8080/product/config
```

在 Nacos 修改 `enterprise-finance-user.yml` 的 `demo.message` 后再次调用，应返回新值（`@RefreshScope`）。

### 双向 Feign

```bash
curl http://localhost:8080/user/1
curl http://localhost:8080/order/user/1
curl http://localhost:8080/user/orders/1
curl http://localhost:8080/product/1
curl http://localhost:8080/product/user/1
curl http://localhost:8080/product/orders/1
```

### 商品链路联调（user -> product -> order）

只读串调（查用户、商品、该用户订单列表）：

```bash
curl http://localhost:8080/product/chain/1/1
```

下单购买（扣库存 + 调 order 创建订单并扣余额）：

```bash
curl -X POST http://localhost:8080/product/purchase ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"productId\":1,\"quantity\":1,\"rollback\":false}"
```

回滚演示（库存、订单、余额均回滚）：

```bash
curl -X POST http://localhost:8080/product/purchase ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"productId\":1,\"quantity\":1,\"rollback\":true}"
```

### 分布式事务（Seata AT）

成功下单（alice 余额 10000，扣 100）：

```bash
curl -X POST http://localhost:8080/order/create ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"amount\":100,\"rollback\":false}"
```

再查 `GET /user/1`，余额减少；`GET /user/orders/1` 能看到订单。

回滚演示（两边都不落库）：

```bash
curl -X POST http://localhost:8080/order/create ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"amount\":50,\"rollback\":true}"
```

余额不变，也不新增订单。

### 熔断 / 降级

异常比例熔断（连续带 `fail=true` 超过 5 次后进入熔断，10 秒窗口）：

```bash
curl "http://localhost:8080/user/sentinel/circuit?fail=true"
curl "http://localhost:8080/order/sentinel/circuit?fail=true"
```

降级：`fail=true` 走 fallback；QPS 超过 2 走 blockHandler：

```bash
curl "http://localhost:8080/user/sentinel/fallback?fail=true"
curl "http://localhost:8080/user/sentinel/fallback"
```

Sentinel 控制台可观察实时规则与簇点。

## MyBatis-Plus 分页使用说明 (XML 方式)

与 `PageHelper` 使用 `ThreadLocal`（如 `PageHelper.startPage(...)`）不同，MyBatis-Plus 采用了**对象传参**的显式分页模式，避免了多线程环境下的分页参数污染问题。

要使用基于 XML 的手写 SQL 分页，只需以下三步（可参考 `IdentifierService.java` 中的实现）：

**第一步：在 Mapper 接口中，将 `Page` 放在第一个参数位置**
```java
// 只要把 Page 对象放在第一个参数，MyBatis-Plus 就会自动识别并拦截该方法的 SQL
Page<Identifier> selectPageByCondition(Page<Identifier> page, @Param("req") IdentifierPageRequest request);
```

**第二步：在 XML 里，正常写 SQL，不要加 LIMIT**
```xml
<select id="selectPageByCondition" resultType="com.enterprise.finance.user.entity.Identifier">
    SELECT * FROM identifier
    <where>
        <if test="req.deviceId != null and req.deviceId != ''">
            AND device_id = #{req.deviceId}
        </if>
    </where>
    ORDER BY id DESC
</select>
<!-- MyBatis-Plus 会在底层自动帮你拼接 LIMIT 和 COUNT 语句 -->
```

**第三步：在 Service 中调用，直接传 Page 对象**
```java
public PageResult<IdentifierDTO> page(IdentifierPageRequest request) {
    // 1. 创建 Page 对象
    Page<Identifier> page = new Page<>(request.getCurrent(), request.getSize());
    
    // 2. 调用查询
    // 拦截器执行完毕后，page 对象的 total(总数) 和 records(列表) 会被自动填满
    Page<Identifier> resultPage = identifierMapper.selectPageByCondition(page, request);
    
    // 3. 封装返回
    return PageResult.of(
            identifierConverter.doToDtoList(resultPage.getRecords()),
            resultPage.getTotal(),
            resultPage.getSize(),
            resultPage.getCurrent()
    );
}
```
