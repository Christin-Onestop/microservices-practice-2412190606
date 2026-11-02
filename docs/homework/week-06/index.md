# 微服务拆分与编排

## 1. 服务拆分设计

### 实现思路

本项目以 CampusRunner 校园跑腿平台为基础，将原有单体应用按照业务职责拆分为用户服务、任务服务和支付服务。用户服务负责用户信息查询，任务服务负责跑腿任务的创建和查询，支付服务负责赏金及支付记录管理。每个微服务使用独立的 Spring Boot Maven 工程、配置文件和数据库，避免跨服务直接访问对方的 Repository 或数据库表。

服务之间通过 HTTP API 通信。任务服务使用 RestTemplate 调用用户服务，验证发布者是否存在，并设置连接超时和读取超时。只有远程用户验证成功后，任务服务才保存任务；如果用户不存在或用户服务不可用，则返回明确的错误信息，不写入本地任务数据。

### 服务拆分方案

- `user-service`：端口 8081，负责用户信息，计划使用独立数据库 `campusrunner_user_db`。
- `task-service`：端口 8082，负责跑腿任务，计划使用独立数据库 `campusrunner_task_db`。
- `payment-service`：端口 8083，负责赏金及支付记录，计划使用独立数据库 `campusrunner_payment_db`。

三个服务之间通过用户 ID、任务 ID 等标识关联数据，不建立跨数据库外键。原有 `monolith/` 项目保留，避免影响此前完成的作业。

### 当前完成情况

已创建 `docs/microservices-design.md`，记录服务职责、端口、数据归属、服务调用关系和 Mermaid 架构图；已创建 `services/` 目录以及用户服务、任务服务的项目目录，并开始实现用户查询、任务创建及跨服务调用。

### 运行命令与结果

在项目根目录执行：

```bash
ls -la services
cat docs/microservices-design.md
```

用于检查微服务目录及拆分设计文档。用户服务测试执行：

```bash
cd services/user-service
mvn test
```

测试结果为 `BUILD SUCCESS`，说明当前用户服务的测试通过。任务服务及支付服务仍需继续完善，后续将通过统一编排和接口测试验证完整调用流程。

## 2. 代码拆分

### 实现思路

采用独立 Maven 工程的方式组织微服务。每个服务分别配置 `pom.xml`、启动类、`application.yml`、Controller、Service、Repository、Entity 和测试代码。用户服务负责查询用户，任务服务负责任务数据及业务校验，支付服务负责支付记录。服务内部通过分层结构组织代码，服务之间通过 HTTP 接口交互。

### 当前完成情况

已创建 `services/user-service/` 和 `services/task-service/` 的基础目录及部分代码。用户服务已实现按 ID 查询用户的接口 `GET /api/users/{id}`，并完成返回 404 的测试。任务服务已实现任务实体、Repository、Service 和 Controller 的初步代码，提供查询任务和创建任务的接口，并开始使用 RestTemplate 调用用户服务验证发布者。

目前代码拆分尚未全部完成：支付服务、全部服务的独立数据库配置、统一 Docker Compose 编排以及完整的跨服务验证仍待完成。

### 运行命令与验证方法

检查服务项目结构：

```bash
cd ~/microservices-practice-2412190606
find services -maxdepth 4 -type f | sort
```

运行用户服务测试：

```bash
cd services/user-service
mvn test
```

运行任务服务测试：

```bash
cd services/task-service
mvn test
```

用户服务测试已返回 `BUILD SUCCESS`。任务服务后续需要继续验证编译、业务逻辑和跨服务调用；待三个服务及编排配置完成后，再执行全部服务测试和接口验证。

### 阶段性测试结果

已为支付服务增加 Service 层单元测试，覆盖正常创建支付记录、非正数金额校验，以及付款人和收款人不能相同等场景。测试使用 Mockito 模拟 Repository，不依赖真实数据库。

测试命令：

```bash
cd services/user-service && mvn test
cd services/task-service && mvn test
cd services/payment-service && mvn test
```

## 三、本地编排与部署（Docker Compose）

### 3.1 Docker Compose 多服务编排

在项目根目录配置 compose.yaml，保留原有单体应用和 MySQL 服务，并新增 user-service、task-service、payment-service 三个微服务容器，以及各自独立的 MySQL 数据库容器。

三个微服务分别使用 8081、8082、8083 端口，三个数据库分别使用独立的数据卷保存数据。通过 Docker Compose 网络实现容器之间的通信，task-service 使用 user-service 的服务名称和容器端口访问用户服务。

### 3.2 启动与健康检查

执行 docker compose up -d --build 构建并启动所有容器。通过 docker compose ps 检查容器运行状态，三个独立数据库均显示 healthy，三个微服务均正常运行。

分别访问以下健康检查接口：

- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health
- http://localhost:8083/actuator/health

三个接口均返回 status 为 UP，数据库组件状态也均为 UP。同时，原有单体应用的 http://localhost:8080/actuator/health 也返回 UP，说明本次微服务部署没有影响原有单体应用。

### 3.3 自动化冒烟测试

在 scripts/smoke-test.sh 中编写自动化冒烟测试脚本，通过 curl 依次访问三个微服务的 Actuator 健康检查接口，并判断返回状态是否为 UP。执行脚本后，三个微服务的健康检查均通过。

### 3.4 跨服务 HTTP 调用验证

通过 task-service 调用 user-service 的 HTTP 接口验证任务发布者是否存在。测试中，首先在用户服务数据库中创建测试用户，并通过 GET /api/users/1 成功获取用户信息，返回 HTTP 200。

随后调用 POST /api/tasks 创建任务。任务服务使用 RestTemplate 请求用户服务，验证发布者存在后保存任务，返回 HTTP 201。

异常场景下，使用不存在的用户 ID 999999 创建任务，接口返回 HTTP 400，并提示用户不存在。再次查询任务列表，未发现异常测试任务，证明用户验证失败时不会写入任务数据。
