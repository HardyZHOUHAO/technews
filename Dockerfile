FROM openjdk:11
COPY . /app
WORKDIR /app
RUN javac -cp jsoup-1.22.2.jar *.java
CMD ["java", "-cp", ".:jsoup-1.22.2.jar", "Server"]
