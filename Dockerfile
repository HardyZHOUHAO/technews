FROM openjdk:11
COPY . /app
WORKDIR /app
RUN curl -L -o jsoup.jar https://repo1.maven.org/maven2/org/jsoup/jsoup/1.17.2/jsoup-1.17.2.jar
RUN javac -cp jsoup.jar App.java
CMD ["java", "-cp", ".:jsoup.jar", "App"]
