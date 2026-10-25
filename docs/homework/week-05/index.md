# Docker 容器化部署

## 一、实验目标

本次作业将 Spring Boot 单体应用与 MySQL 数据库进行容器化部署，实现统一构建、启动、健康检查和数据持久化。

## 二、项目配置

### 1. Dockerfile

在 `monolith/Dockerfile` 中使用多阶段构建：

- 构建阶段使用 Maven 和 Java 25 编译 Spring Boot 项目。
- 运行阶段使用 Java 25 JRE 镜像运行打包后的 JAR 文件。
- 应用容器监听 8080 端口。

### 2. Docker Compose

在项目根目录创建 `compose.yaml`，定义两个服务：

- `app`：运行 Spring Boot 应用，对外映射 8080 端口。
- `mysql`：运行 MySQL 8.4.11，提供应用所需的数据库。

应用通过服务名 `mysql` 连接数据库，而不是使用容器内部的 `localhost`。

### 3. 环境变量与数据持久化

使用 `.env.example` 提供环境变量模板，通过本地 `.env` 配置数据库名称、用户名、密码和应用端口。

使用命名卷 `mysql_data` 保存 MySQL 数据。执行 `docker compose down` 后再启动容器，数据库数据仍然保留。

实际 `.env` 文件已配置为 Git 忽略文件，避免提交本地环境变量。

## 三、部署与验证

### 1. 启动服务

使用以下命令构建镜像并启动容器：

```bash
docker compose up -d --build
```

查看容器运行状态：

```bash
docker compose ps
```

验证结果：应用容器正常运行，MySQL 容器状态为 `healthy`。

### 2. 健康检查

请求：

```bash
curl -i http://localhost:8080/actuator/health
```

验证结果：接口返回 HTTP 200，整体状态为 `UP`，数据库组件状态为 `UP`。

### 3. 任务列表查询

请求：

```bash
curl -i http://localhost:8080/api/tasks
```

验证结果：接口返回 HTTP 200，任务列表中包含已创建的测试任务。

### 4. 单条任务查询

请求：

```bash
curl -i http://localhost:8080/api/tasks/1
```

验证结果：接口返回 HTTP 200，返回任务 ID、标题、描述、奖励、发布者和状态等信息。

### 5. 数据持久化验证

先通过业务 API 创建测试任务，再执行：

```bash
docker compose down
docker compose up -d
docker compose ps
curl -i http://localhost:8080/api/tasks/1
```

验证结果：容器重建后，MySQL 数据仍然保留，任务 ID 为 1 的记录可以正常查询。

注意：不要使用 `docker compose down -v` 进行本次持久化验证，因为该命令会删除 Compose 管理的命名卷。

## 四、实验总结

本次作业完成了 Spring Boot 应用和 MySQL 数据库的 Docker 容器化部署。通过 Docker Compose 统一管理服务，利用健康检查控制应用启动依赖，并使用 MySQL 命名卷实现数据持久化。健康检查、任务列表查询、单条任务查询和容器重建后的数据查询均已验证成功。