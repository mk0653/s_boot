#FROM ubuntu:18.04

# ベースイメージ（JDK 17）
FROM openjdk:17-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app	

# JAR をコピー
COPY /EC_cat/target/EcCat-0.0.1-SNAPSHOT.jar app.jar

# ポート 8088 を開放
EXPOSE 8088

# JAR を実行
ENTRYPOINT ["java", "-jar", "app.jar"]