# Bước 1: Build ứng dụng
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY src/main/java .
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
# Thêm cấu hình Xmx để giới hạn RAM Java sử dụng trong mức 512MB của Render
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-jar", "app.jar"]