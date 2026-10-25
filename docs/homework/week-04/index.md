## 业务建模

### 业务场景

CampusRunner 的核心业务场景是学生发布校园跑腿任务，由其他学生接单并完成跑腿服务。本周优先实现任务发布与接单流程，暂不涉及真实支付、评价和通知功能。

### 核心流程

1. 发单学生提交任务标题、任务描述和赏金。
2. 系统检查请求参数，并将任务保存到数据库，初始状态设置为 `OPEN`（待接单）。
3. 跑腿员选择一个任务并提交接单请求。
4. 系统在 Service 层检查任务是否存在、是否仍处于 `OPEN` 状态，以及跑腿员是否为该任务的发布者。
5. 如果检查通过，系统记录接单人，并将任务状态更新为 `CLAIMED`（已接单）。
6. 如果任务不存在、已经被接单或不允许当前用户接单，系统返回明确的业务错误，不执行无效的状态变更。

### 业务实体及其关系

#### User（用户）

用于表示 CampusRunner 中的学生用户。

主要属性包括：

- `id`：用户唯一标识。
- `username`：用户名。
- `role`：用户角色，例如发单方或跑腿员。

#### Task（跑腿任务）

用于记录学生发布的跑腿需求。

主要属性包括：

- `id`：任务唯一标识。
- `title`：任务标题。
- `description`：任务描述。
- `reward`：任务赏金。
- `status`：任务状态，初始为 `OPEN`，接单成功后变为 `CLAIMED`。
- `publisherId`：发布该任务的用户标识。
- `runnerId`：接单用户标识；尚未接单时为空。

#### 实体关系

一个 `User` 可以发布多个 `Task`，每个 `Task` 必须关联一个发布者。一个 `User` 也可以承接多个任务，但每个任务最多只能记录一个接单人。发布者和接单人均通过用户标识与 `User` 关联；本周不实现用户认证及真实身份校验。

### Service 层业务规则

核心规则：只有状态为 `OPEN` 的任务才能被接单。接单成功后，系统必须记录接单用户，并将任务状态更新为 `CLAIMED`。

Service 层还需要检查以下条件：

1. 任务必须存在。
2. 任务必须处于待接单状态。
3. 发布者不能接单自己的任务。
4. 不满足条件时，应返回明确的业务错误，且不得错误地更新任务状态。

在后续实现中，还需要通过事务和适当的并发控制，避免多个用户同时接单导致同一任务被重复承接。

### 本周建模结论

本周采用 `User` 和 `Task` 两个业务实体描述用户与跑腿任务之间的关系，以任务状态表示接单流程的进展。任务发布和接单逻辑由 Service 层统一管理，数据库负责持久化任务信息和接单结果。该设计为后续实现订单、支付、通知及微服务拆分提供基础。

## 单体应用实现

### 实现思路

本周在现有 Spring Boot 单体工程的基础上实现 CampusRunner 的任务发布与接单功能。应用采用分层结构，将业务模型、数据访问、业务规则和 HTTP 接口分开管理。

1. **Entity 层**：创建 `User` 和 `Task` 两个 JPA 实体，分别表示学生用户和跑腿任务。任务通过发布者和接单者关联用户。
2. **Repository 层**：创建 `UserRepository` 和 `TaskRepository`，使用 Spring Data JPA 提供的数据库访问方法。
3. **Service 层**：创建 `TaskService`，负责任务创建、任务列表查询、任务详情查询和接单业务，并检查任务状态、用户是否存在以及发布者是否试图接单自己的任务。
4. **Controller 层**：创建 `TaskController`，通过 REST API 接收请求并返回响应，不直接编写数据库访问逻辑。
5. **数据持久化**：使用 H2 文件数据库保存业务数据，并通过 JPA 配置管理数据库表结构。

### 已实现的 REST 接口

| 方法 | 路径 | 功能 |
|---|---|---|
| POST | `/api/tasks` | 创建跑腿任务 |
| GET | `/api/tasks` | 查询任务列表 |
| GET | `/api/tasks/{id}` | 查询任务详情 |
| POST | `/api/tasks/{id}/claim` | 接取跑腿任务 |

### 运行命令

在项目根目录执行：

```bash
./monolith/mvnw -f monolith/pom.xml compile
```

启动应用：

```bash
cd monolith
./mvnw spring-boot:run
```

在另一个终端验证接口：

```bash
curl -i http://localhost:8080/api/hello
curl -i http://localhost:8080/api/tasks
curl -i http://localhost:8080/actuator/health
```

### 运行结果记录

完成编译和接口验证后，在此记录实际结果：

- 编译结果：填写 Maven 实际输出，例如 `BUILD SUCCESS`。
- 问候接口：记录实际 HTTP 状态码和响应内容。
- 任务列表接口：记录实际 HTTP 状态码及返回的 JSON。
- 健康检查：记录实际 HTTP 状态码和健康状态。
- 数据持久化：确认应用启动后数据库文件实际生成的位置，并验证数据重启后是否保留。

### 本周实现总结

本周将任务发布和接单功能划分为 Entity、Repository、Service 和 Controller 四个层次，形成单体应用的基本业务结构。任务接单规则集中在 Service 层，为后续补充并发控制、用户认证、订单管理和支付功能提供基础。


## 数据库持久化

### 1. 实现思路

本项目使用 MySQL 保存 CampusRunner 的用户和任务数据，使用 Spring Data JPA 和 Hibernate 完成对象关系映射与数据库访问。

主要实现包括：

- `entity/User.java`：定义用户实体，对应 `users` 表。
- `entity/Task.java`：定义任务实体，对应 `tasks` 表。
- `repository/UserRepository.java`：提供用户数据访问能力。
- `repository/TaskRepository.java`：提供任务数据访问能力。
- `service/TaskService.java`：处理任务创建、查询和领取等业务逻辑。
- `controller/TaskController.java`：提供任务相关 HTTP API。
- `application.yml`：配置 MySQL 数据源和 JPA。

用户表通过唯一约束保证用户名不重复。任务表通过 `publisher_id` 和 `runner_id` 关联发布者和领取者，其中尚未领取的任务允许 `runner_id` 为空。

数据库结构说明见 [database.md](../../database.md)。

### 2. 数据库配置与运行命令

本地使用 MySQL 8.x，数据库名称为 `campusrunner`，应用账号为 `campusrunner_app`。

在项目根目录启动应用前，先设置数据库连接环境变量：

```bash
export DB_USERNAME=campusrunner_app
read -rsp "请输入 MySQL 密码: " DB_PASSWORD
echo
export DB_PASSWORD
```


## 测试与验证

### 1. Repository 层测试

使用 JUnit 对 `UserRepository` 进行测试，验证数据库的基本读写功能。

- **保存并查询用户**：创建测试用户 `test_alice`，将其保存到数据库，再根据用户名查询，验证保存和查询功能正常。
- **查询不存在的用户**：使用不存在的用户名进行查询，验证 Repository 能够正确返回空结果。

测试使用 H2 内存数据库，避免依赖本地 MySQL 数据库中的实际业务数据，提高测试的独立性和可重复性。

### 2. Service 层测试

使用 JUnit 和 Mockito 对 `TaskService` 进行单元测试，通过模拟 Repository 依赖验证业务规则。

- **任务标题校验**：当任务标题为空白内容时，系统应拒绝创建任务，并抛出 `IllegalArgumentException`。
- **任务奖励校验**：当任务奖励小于或等于 0 时，系统应拒绝创建任务，并抛出 `IllegalArgumentException`。
- **领取任务规则**：当用户尝试领取自己发布的任务时，系统应拒绝该操作，并抛出 `IllegalArgumentException`。

以上测试验证了任务创建和领取过程中的关键业务约束，确保非法操作能够被正确拦截。

### 3. API 层测试

使用 Spring Boot 测试环境和 MockMvc 对任务接口进行测试，验证 HTTP 请求及响应状态。

- **查询任务列表**：向 `GET /api/tasks` 发送请求，预期返回 `200 OK`，验证任务列表接口能够正常访问。
- **查询不存在的任务**：向 `GET /api/tasks/999999` 发送请求，预期返回 `404 Not Found`，验证系统能够正确处理不存在的任务。

### 4. 测试环境与执行方式

测试使用独立的 `test` 配置文件和 H2 内存数据库，避免测试依赖开发环境中的 MySQL 数据。Repository 测试验证数据访问功能，Service 测试验证业务规则，API 测试验证接口的 HTTP 响应，形成分层测试结构。

在 `monolith` 目录下执行以下命令运行全部测试：

```bash
./mvnw test
```

本次执行结果为 `BUILD SUCCESS`，说明当前测试套件执行成功。后续修改代码后，可以重复执行该命令，检查代码变更是否影响现有功能。