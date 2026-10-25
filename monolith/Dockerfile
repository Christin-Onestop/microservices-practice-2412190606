# 构建阶段：使用 Maven 和 Java 25 编译项目
FROM maven:3.9-eclipse-temurin-25 AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

COPY src ./src
RUN ./mvnw -B -DskipTests package

# 运行阶段：只保留运行 Java 应用所需的 JRE
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
