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