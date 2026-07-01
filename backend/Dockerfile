FROM maven:3.8.8-eclipse-temurin-8 AS builder

WORKDIR /workspace/backend

COPY backend/pom.xml ./
COPY backend/.mvn ./.mvn
COPY backend/mvnw backend/mvnw.cmd ./
COPY backend/src ./src

RUN mvn -B -DskipTests package \
    && cp target/yuoj-backedn-0.0.1-SNAPSHOT.jar /tmp/app.jar

FROM eclipse-temurin:8-jre AS runner

WORKDIR /app/backend

ENV TZ=Asia/Shanghai \
    SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8121

COPY --from=builder /tmp/app.jar ./app.jar
COPY src/assets /app/src/assets

RUN mkdir -p /app/backend/uploads

EXPOSE 8121

ENTRYPOINT ["java", "-jar", "app.jar"]
