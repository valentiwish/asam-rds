#镜像打包命令：mvn clean package dockerfile:build -Dmaven.test.skip=true
#基础镜像，如果本地没有，会从远程仓库拉取。
FROM openjdk:8-jdk-alpine

#指定镜像的作者信息，包含镜像的所有者和联系人信息
MAINTAINER zl2020.9.17

#工作目录，在容器内部设置工作目录，这样ENTRYPOINT和CMD指定的命令都会在容器中这个目录下进行。
WORKDIR /app/
#addgroup====》-S：创建系统组
#adduser=====》-S：创建系统用户，-G：指定用户所属的组
#RUN addgroup -S spring && adduser -S spring -G spring

#用于设置运行容器的用户与组
#USER spring:spring

#在容器中创建挂载点，可以多个
VOLUME ["/tmp"]

#指定运行该镜像的容器使用的端口，可以是多个，声明了容器应该打开的端口并没有实际上将它打开
EXPOSE 5084-9000

#ARG JARFILE=target/*.jar
#定义参数
ARG JAR_FILE

#拷贝本地文件到镜像中app.jar中
COPY ${JAR_FILE} app.jar

#入口点：指定容器启动时要执行的命令，但如果存在CMD指令，CMD中的参数会被附加到ENTRYPOINT指令的后面
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]

#---------------------------------------------------------------------------------
#用于指定构建镜像时运行的命令
#RUN yum install httpd && yum install ftp
#用于提供容器运行的默认命令，如果在docker run时指定了运行的命令，则CMD命令不会执行
#CMD [ "executable", "param1", "param2" ] (exec模式)
#java -D 配置系统属性。使用格式是：java -Dkey=value
#-D 是新增参数，参数的名字叫 java.security.egd ，参数的值叫 file:/dev/./urandom，是为了解决随机数生成的问题

#ADD和COPY
#COPY <src> <dest>
#ADD ["<src>" "<dest>"] (适用于文件路径包含空格的情况)