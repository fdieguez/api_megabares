FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/api_megabares-1.0.jar
copy ${JAR_FILE} app_apimegabares.jar
expose 8080
entrypoint ["java","-jar","app_apimegabares.jar"]
