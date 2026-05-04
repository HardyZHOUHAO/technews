FROM eclipse-temurin:17-jdk-alpine

# 创建工作目录
WORKDIR /app

# 创建 lib 目录
RUN mkdir -p lib

# 下载 Jsoup
RUN wget -O lib/jsoup.jar https://repo1.maven.org/maven2/org/jsoup/jsoup/1.15.3/jsoup-1.15.3.jar

# 下载 Gson
RUN wget -O lib/gson.jar https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar

# 复制代码
COPY App.java .

# 编译
RUN javac -cp "lib/*" App.java

# 运行
CMD java -cp "lib/*:." App
