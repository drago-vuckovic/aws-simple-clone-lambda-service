FROM openjdk:17
COPY target/*.jar lambdaService.jar
ENTRYPOINT ["java", "-jar", "/lambdaService.jar", "--spring.profiles.active=prod"]