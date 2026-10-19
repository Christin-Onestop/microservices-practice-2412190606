## 一、本周完成内容

本周任务主要围绕“项目规划”与“Spring Boot 工程搭建”展开，具体完成内容如下：

1. **明确项目提案**
   - 在 `docs/project-proposal.md` 中确定了项目名称为 CampusRunner 校园跑腿服务平台。
   - 梳理了主要目标用户：学生（发单方）、跑腿员（接单方）、平台管理员。
   - 明确了本周优先规划的业务场景：“发布代取快递任务 → 抢单 → 完成 → 确认 → 支付”。
   - 规划了两个核心模型：`Task`（跑腿任务）和 `Order`（任务订单）。

2. **创建 Spring Boot 单体工程**
   - 在仓库根目录创建 `monolith/` 目录，使用 Spring Initializr 生成 Maven 工程。
   - 使用 Java 25 + Spring Boot 4.0.8，Group 和 Package name 均为 `com.zjgsu.tyt`。
   - 引入依赖：`spring-boot-starter-web`（Web MVC）和 `spring-boot-starter-actuator`（健康检查）。
   - 将默认的 `application.properties` 替换为 `application.yml`。

3. **编写接口与配置**
   - 添加启动类 `MonolithApplication.java`。
   - 编写一个基础 GET 接口 `/api/hello`，返回项目名称和问候消息。
   - 配置 `application.yml`，默认端口 `8080`，并暴露 Actuator 的 `health` 和 `info` 端点。

4. **环境验证与测试**
   - 在 `monolith/` 目录内成功执行 `./mvnw test`，测试通过。
   - 成功执行 `./mvnw spring-boot:run` 启动应用。
   - 通过 `curl` 验证 `/api/hello` 和 `/actuator/health` 均正常返回。
   - 完成代码的 Git 提交并推送到 GitHub 仓库