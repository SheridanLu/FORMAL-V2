# 多阶段构建 — 后端
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY mochu-common/pom.xml mochu-common/pom.xml
COPY mochu-framework/pom.xml mochu-framework/pom.xml
COPY mochu-system/pom.xml mochu-system/pom.xml
COPY mochu-business/pom.xml mochu-business/pom.xml
COPY mochu-admin/pom.xml mochu-admin/pom.xml
RUN mvn dependency:go-offline -B
COPY . .
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/mochu-admin/target/mochu-admin-*.jar app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
