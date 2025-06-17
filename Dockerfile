# Gradle로 빌드된 JAR 파일을 실행하기 위한 Dockerfile
FROM openjdk:17-jdk-slim

# JAR 파일 이름 설정 (build/libs/ 경로 기준으로 수정)
ARG JAR_FILE=build/libs/backend-api-0.0.1-SNAPSHOT.jar

# JAR 파일을 컨테이너 내부로 복사
COPY ${JAR_FILE} app.jar

# 8080 포트 개방 (Spring Boot 기본 포트)
EXPOSE 8080

# 컨테이너 실행 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
