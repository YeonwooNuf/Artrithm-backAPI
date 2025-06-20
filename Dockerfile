FROM openjdk:17-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
RUN curl -s https://get.sdkman.io | bash

# ✅ 로컬에서 마운트될 것이므로 COPY 제거
RUN chmod +x ./gradlew || true

CMD ["./gradlew", "bootRun"]