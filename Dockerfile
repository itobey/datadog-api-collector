FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
COPY target/datadog-api-0.1.jar /datadog-api-gatherer/app.jar
EXPOSE 8080

RUN apk add --no-cache tzdata
ENV TZ=Europe/Berlin

CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar /datadog-api-gatherer/app.jar
