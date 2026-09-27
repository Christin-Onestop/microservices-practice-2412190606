## 环境检查

onestop@Onestop:~$ java --version
openjdk 26.0.2 2026-07-21
OpenJDK Runtime Environment (build 26.0.2+10-2-26.04.2-Ubuntu)
OpenJDK 64-Bit Server VM (build 26.0.2+10-2-26.04.2-Ubuntu, mixed mode, sharing)
onestop@Onestop:~$ mvn --version
Apache Maven 3.9.16 (2bdd9fddda4b155ebf8000e807eb73fd829a51d5)
Maven home: /opt/maven
Java version: 26.0.2, vendor: Ubuntu, runtime: /usr/lib/jvm/java-26-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.18.33.2-microsoft-standard-wsl2", arch: "amd64", family: "unix"
onestop@Onestop:~$ git --version
git version 2.53.0
onestop@Onestop:~$ docker version
Client:
 Version:           29.8.0
 API version:       1.56
 Go version:        go1.26.8
 Git commit:        88096ef
 Built:             Thu Sep  3 21:49:51 2026
 OS/Arch:           linux/amd64
 Context:           default

Server: Docker Desktop 4.91.0 (239619)
 Engine:
  Version:          29.8.0
  API version:      1.56 (minimum version 1.40)
  Go version:       go1.26.8
  Git commit:       3ce5872
  Built:            Thu Sep  3 21:51:20 2026
  OS/Arch:          linux/amd64
  Experimental:     false
 containerd:
  Version:          v2.3.4
  GitCommit:        db8809540e1a7a9da5d518876894933ff55692ab
 runc:
  Version:          1.4.3
  GitCommit:        v1.4.3-0-gbb14dabe
 docker-init:
  Version:          0.19.0
  GitCommit:        de40ad0
onestop@Onestop:~$ docker compose version
Docker Compose version v5.5.1

## 概念回答

1.什么是微服务架构？
微服务架构是把一个大型应用拆分成多个小型、独立部署的服务，每个服务围绕业务功能构建，运行在自己的进程中，通过轻量级机制（如 HTTP/REST）通信，可以由小团队独立开发、部署和扩展。

2.微服务和单体架构的主要区别是什么？
单体架构所有功能打包在一个应用中，共享数据库，集中管理，部署整体进行；微服务则按业务拆分，每个服务独立部署、独立数据库、技术栈灵活，通过 API 通信。单体初期简单，微服务扩展性和团队自治更好，但分布式复杂性更高。

3.为什么本课程先实现单体系统，再逐步拆分为微服务？
先实现单体可以快速理解业务需求和整体流程，降低初期复杂度；再逐步拆分，能对比两种架构的差异，掌握服务拆分、通信、治理等微服务核心技能，避免一开始就陷入分布式复杂性。

4.为什么作业需要提供可重复运行的测试或验证脚本？
可重复运行的测试或脚本能保证环境检查、功能验证的一致性，方便教师和同学复现结果，也便于后续持续集成和自动化部署，减少人为操作错误。

## 问题记录

无

## 项目选题
    在大学校园中，代取快递、代买饭菜、代送文件、代还图书等跑腿需求非常普遍。同时，有的大学生愿意付出一些小小的劳动来赚一点零花钱。以此为背景我想做一个校园跑腿服务平台，这个选题业务逻辑容易理解，需求调研和功能设计不需要依赖复杂行业知识，便于把精力集中在微服务架构实践上。
    其次，从用户注册、发布任务、抢单、支付、完成、通知到评价，形成完整闭环。每个环节都可以对应一个或多个微服务。与课程内容比较契合，于是我便确定了这个选题。

## 功能规划

本周完成内容：
  确定课程项目选题：CampusRunner 校园跑腿服务平台。完成项目简介、选题说明、业务背景与选择原因。完成用户角色、功能模块、核心业务流程设计。

后续计划：
第3周	单体最小功能：用 Spring Boot 写单体版：用户注册登录、发布任务、任务列表、抢单、创建订单、模拟支付
第4周	单体完善与拆分设计：完善状态流转；整理 REST 接口；画服务拆分图、数据库拆分图、调用关系图
第5周	微服务骨架：搭建 Maven 父工程；创建 user-service、task-service；各自独立启动
第6周	继续拆分：创建 order-service、payment-service、notification-service；独立数据库
第7周	服务注册与配置：引入 Nacos Discovery、Nacos Config；所有服务注册到 Nacos
第8周	网关与调用：引入 Spring Cloud Gateway；OpenFeign 声明式调用；统一路由与鉴权入口
第9周	负载均衡与容错：引入 LoadBalancer；Resilience4j 断路器、重试、限流
第10周 异步消息：引入 RabbitMQ 或 Kafka；任务状态变更、通知服务异步消费
第11周 分布式事务：引入 Seata 或本地消息表；保证“支付成功→订单状态更新→任务状态更新”一致性
第12周 缓存与并发：Redis 缓存任务大厅；分布式锁处理抢单并发；延迟消息处理超时订单
第13周 监控与链路追踪：Micrometer Tracing + Zipkin 链路追踪；Prometheus + Grafana 监控；日志聚合
第14周 容器化部署：编写 Dockerfile；Docker Compose 一键启动所有服务、Nacos、MySQL、Redis、RabbitMQ；编写验证脚本
第15周 文档：完善 README、接口文档、部署文档