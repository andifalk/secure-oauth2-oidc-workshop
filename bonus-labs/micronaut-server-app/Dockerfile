FROM openjdk:14-alpine
COPY build/libs/micronaut-server-app-*-all.jar micronaut-server-app.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "micronaut-server-app.jar"]
