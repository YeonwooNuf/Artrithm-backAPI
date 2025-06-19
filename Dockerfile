FROM openjdk:17-jdk-slim

WORKDIR /app

# bash 설치 추가 (slim 이미지엔 기본적으로 없음!)
RUN apt-get update && apt-get install -y bash