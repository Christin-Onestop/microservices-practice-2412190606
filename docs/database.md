# CampusRunner 数据库持久化设计

## 1. 实现目标

本项目使用 MySQL 作为关系型数据库，通过 Spring Data JPA 实现业务数据的持久化存储。应用程序通过 Entity、Repository 和 Service 分层访问数据库，避免将业务数据仅保存在内存中。

## 2. 数据库配置

- 数据库名称：`campusrunner`
- 数据库类型：MySQL 8.x
- ORM 框架：Hibernate
- 数据访问框架：Spring Data JPA
- 数据库驱动：MySQL Connector/J
- 数据库连接地址：`jdbc:mysql://localhost:3306/campusrunner`

数据库连接的用户名和密码通过环境变量配置，避免将真实密码直接写入项目配置文件或提交到 Git 仓库。

主要配置如下：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campusrunner?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
    username: ${DB_USERNAME:campusrunner_app}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
    show-sql: true
```

其中，`ddl-auto: update` 用于根据实体类更新数据库表结构，`show-sql: true` 用于输出 Hibernate 执行的 SQL，便于开发阶段检查数据库访问情况。

## 3. 数据表设计

### 3.1 用户表 users

用户表用于保存校园跑腿平台的用户信息。

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | BIGINT | 主键，由数据库自动生成 |
| `username` | VARCHAR(50) | 用户名，不能为空且唯一 |
| `role` | VARCHAR(20) | 用户角色，不能为空 |

约束说明：

- `id` 为主键，使用自增方式生成。
- `username` 设置唯一约束，防止重复用户名。
- `username` 和 `role` 均不能为空。

### 3.2 任务表 tasks

任务表用于保存用户发布的校园跑腿任务及任务领取情况。

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | BIGINT | 主键，由数据库自动生成 |
| `title` | VARCHAR(100) | 任务标题，不能为空 |
| `description` | VARCHAR(1000) | 任务描述，可为空 |
| `reward` | DECIMAL(10,2) | 任务报酬，不能为空 |
| `status` | ENUM | 任务状态，包括 OPEN 和 CLAIMED |
| `publisher_id` | BIGINT | 发布任务的用户 ID，不能为空 |
| `runner_id` | BIGINT | 领取任务的用户 ID，可为空 |

约束说明：

- `id` 为自增主键。
- `publisher_id` 对应发布者，必须关联到已存在的用户。
- `runner_id` 对应领取者；任务尚未被领取时允许为空。
- `reward` 用于保存金额，采用定点数类型，避免使用浮点数直接存储金额。
- `status` 用于记录任务当前状态。

## 4. 表之间的关系

`users` 和 `tasks` 之间存在两种业务关联：

1. 发布任务：一个用户可以发布多个任务，每个任务必须有一个发布者。
2. 领取任务：一个用户可以领取多个任务，每个任务最多记录一个领取者；未领取的任务没有领取者。

关系可以概括为：

```text
users (1) ───── (N) tasks
         publisher_id

users (1) ───── (N) tasks
         runner_id（可为空）
```

在 Java 实体类中，任务通过 `ManyToOne` 关联发布者和领取者；Repository 负责数据访问，Service 负责任务创建和领取等业务逻辑。

## 5. Repository 数据访问

项目使用 Spring Data JPA 提供数据访问能力：

- `UserRepository` 继承 `JpaRepository<User, Long>`，负责用户数据的查询与持久化。
- `TaskRepository` 继承 `JpaRepository<Task, Long>`，负责任务数据的查询与持久化。

任务 Repository 支持按状态、发布者 ID 和领取者 ID 查询任务。Service 层调用 Repository 完成业务操作，Controller 层提供 HTTP API。

## 6. 本地数据库初始化

首先安装并启动 MySQL，然后创建数据库和应用账号：

```sql
CREATE DATABASE IF NOT EXISTS campusrunner
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'campusrunner_app'@'localhost'
IDENTIFIED BY '请替换为自己设置的强密码';

GRANT ALL PRIVILEGES ON campusrunner.*
TO 'campusrunner_app'@'localhost';
```

执行建库语句时，应将示例密码替换为自己设置的密码，不要将真实密码提交到仓库。

在启动应用的终端中设置连接参数：

```bash
export DB_USERNAME=campusrunner_app
read -rsp "请输入 MySQL 密码: " DB_PASSWORD
echo
export DB_PASSWORD
```

然后在项目根目录执行：

```bash
./monolith/mvnw -f monolith/pom.xml spring-boot:run
```

成功启动后，可通过以下地址访问任务查询接口：

```text
http://localhost:8080/api/tasks
```

## 7. 数据持久化验证

首先检查数据库中的表：

```sql
USE campusrunner;
SHOW TABLES;
DESCRIBE users;
DESCRIBE tasks;
```

检查用户和任务记录：

```sql
SELECT id, username, role FROM users ORDER BY id;

SELECT id, title, reward, status, publisher_id, runner_id
FROM tasks
ORDER BY id;
```

通过 HTTP 接口创建任务后，再查询 `tasks` 表，确认新增记录已保存到 MySQL。随后重启 Spring Boot 应用，再次查询任务列表，检查数据是否仍然存在。

只有在完成上述查询并确认结果后，才能认定对应的持久化验证已经通过。

## 8. 安全与后续改进

- 数据库密码通过环境变量传入，不应提交真实凭据。
- 开发阶段使用 `ddl-auto: update`；正式部署时应采用受控的数据库迁移方案。
- 测试数据应采用可重复执行的准备脚本或自动化测试。
- 后续可增加数据访问测试、关联约束检查和任务领取并发控制。
