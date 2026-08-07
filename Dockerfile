FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package

FROM tomcat:9.0-jre11-temurin
RUN rm -rf ${CATALINA_HOME}/webapps/*
COPY --from=build /src/target/CSCI6810.war ${CATALINA_HOME}/webapps/CSCI6810.war
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=30s \
  CMD curl -fsS http://localhost:8080/CSCI6810/login.html || exit 1
CMD ["catalina.sh", "run"]
