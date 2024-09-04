FROM eclipse-temurin:21
RUN mkdir /opt/app
COPY target/collector-*.jar /opt/app/app.jar

EXPOSE 8080

#RUN apk add --no-cache tzdata
ENV TZ=Europe/Berlin
CMD ["java", "-jar", "/opt/app/app.jar"]