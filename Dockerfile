# Bước 1: Build ứng dụng
FROM maven:3.9.6-eclipse-temurin-21 AS build
# Thay vì chỉ COPY src, ta COPY toàn bộ thư mục (bao gồm cả pom.xml và src)
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Chạy ứng dụng
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Lưu ý: Maven build xong sẽ nằm trong thư mục target/
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-jar", "app.jar"]