# 빌드 스테이지
FROM gradle:8.6-jdk17 AS build
WORKDIR /app
# 의존성 캐시 최적화
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon || true

# 소스는 마지막에 복사하여 소스 변경 시 빌드만 다시
COPY src ./src
COPY firebase-admin.json ./firebase-admin.json
RUN gradle clean build -x test --no-daemon

# 런타임 스테이지
FROM openjdk:17-jdk-slim
WORKDIR /app
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY build/libs/mymate-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]