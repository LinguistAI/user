FROM openjdk:17
ADD target/linguistai.jar linguistai.jar
ENTRYPOINT ["java","-jar","/linguistai.jar"]