FROM openjdk:11
EXPOSE 8000 8080
ADD target/springboot-keycloak-docker.jar springboot-keycloak-docker.jar
ENTRYPOINT ["java","-jar","/springboot-keycloak-docker.jar"]