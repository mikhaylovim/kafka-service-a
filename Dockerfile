FROM openjdk:21-slim
COPY target/kafka-service-a-0.0.1-SNAPSHOT.jar kafka-service-a-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/kafka-service-a-0.0.1-SNAPSHOT.jar"]
