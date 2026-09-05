# enterprise-finance-parent

Spring Boot 3.2 + Spring Cloud 2023.0.x 微服务骨架，包含 Nacos、Nacos Config、LoadBalancer、OpenFeign、Sentinel、Gateway、Seata。

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
| enterprise-finance-gateway | 8080 | 网关，`/user/**`、`/order/**` |
| enterprise-finance-user | 8081 | 用户服务，库 `user` |
| enterprise-finance-order | 8082 | 订单服务，库 `order` |
| enterprise-finance-common | - | 统一响应、DTO、Feign 契约 |

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

或手动执行 `deploy/sql` 下 `01` 到 `04` 的 SQL。

3. 启动中间件：

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

IDE 运行各模块 `*Application`，或：

```powershell
java -jar enterprise-finance-gateway\target\enterprise-finance-gateway-1.0.0-SNAPSHOT.jar
java -jar enterprise-finance-user\target\enterprise-finance-user-1.0.0-SNAPSHOT.jar
java -jar enterprise-finance-order\target\enterprise-finance-order-1.0.0-SNAPSHOT.jar
```

启动后在 Nacos 服务列表应看到 `enterprise-finance-gateway`、`enterprise-finance-user`、`enterprise-finance-order`、`seata-server`。

## 验收接口

以下请求都走网关 `http://localhost:8080`。

### 配置中心

```bash
curl http://localhost:8080/user/config
curl http://localhost:8080/order/config
```

在 Nacos 修改 `enterprise-finance-user.yml` 的 `demo.message` 后再次调用，应返回新值（`@RefreshScope`）。

### 双向 Feign

```bash
curl http://localhost:8080/user/1
curl http://localhost:8080/order/user/1
curl http://localhost:8080/user/orders/1
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
