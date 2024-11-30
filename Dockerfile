# 1. OpenJDK 17 이미지 사용
FROM openjdk:17

## 2. 애플리케이션 실행에 필요한 디렉토리 생성
#WORKDIR /app

# 3. JAR 파일 복사
COPY build/libs/app.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]