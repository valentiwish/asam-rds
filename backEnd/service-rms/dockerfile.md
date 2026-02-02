#镜像名称规则---需要特别注意，否则制作镜像失败	
	<imageName>push.project.2docker</imageName>imageName 标签内必须严格遵循：
	[a-z0-9-_.]，也就是说只能出现 a~z 小写字母，0~9，下划线"_" 和 点"."
#dockerfile 命令格式
	基础指令
	FROM
	　　基于哪个镜像
	MAINTAINER
	　　用来写备注信息，例如作者、日期等。
	COPY
	　　复制文件进入镜像（只能用相对路径，不能用绝对路径）
	ADD
	　　复制文件进入镜像（可以用绝对路径，假如是压缩文件会解压）
	WORKDIR
	　　指定工作目录，假如路径不存在会创建路径
	ENV
	　　设置环境变量
	EXPOSE
	　　暴露容器端口到宿主机
	RUN
	　　在构建镜像的时候执行一条命令，作用于镜像层面
	　　shell命令格式：RUN yum install -y net-tools
	　　exec命令格式：RUN [ "yum","install" ,"-y" ,"net-tools"]
	ENTRYPOINT
	　　在容器启动的时候执行，作用于容器层，dockerfile里有多条时只允许执行最后一条
	CMD
	　　在容器启动的时候执行，作用于容器层，dockerfile里有多条时只允许执行最后一条
	　　容器启动后执行默认的命令或者参数，允许被修改

# dokerfile 配置 
命令参考地址：https://www.bookstack.cn/read/docker-practice/image-dockerfile-arg.md

	#基础镜像，如果本地没有，会从远程仓库拉取。
	FROM openjdk:8-jdk-alpine
	
	#镜像的制作人
	MAINTAINER test
	
	#工作目录
	WORKDIR /app/
	
	#在容器中创建挂载点，可以多个
	VOLUME ["/tmp"]
	
	#声明了容器应该打开的端口并没有实际上将它打开，该端口应该需要与server.port=5084 端口配置保持一致
	EXPOSE 5084
	
	#定义参数
	ARG JAR_FILE	
	
	#拷贝本地文件到镜像中
	COPY ${JAR_FILE} app.jar
	
	#指定容器启动时要执行的命令，但如果存在CMD指令，CMD中的参数会被附加到ENTRYPOINT指令的后面
	ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","app.jar"]
	
	


	
#配置centos8中docker主机，开放远程端口
	
	# vim编辑docker配置文件/lib/systemd/system/docker.service，并修改ExecStart为下面的内容
	# vim /lib/systemd/system/docker.service
	ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock
	
	修改后，然后重启docker服务
	# 1，加载docker守护线程
	systemctl daemon-reload
	# 2，重启docker 
	systemctl restart docker
#开发环境配置
	#为你打包工程的电脑配置环境变量
	1.添加DOCKER_HOST，值为tcp://xx.xx.xx.xx:2375 
	2、使用maven编译打包镜像 打开cmd窗口，确定环境变量配置生效：输入 echo %DOCKER_HOST%，会输出 tcp://xx.xx.xx.xx:2375 
	3.然后使用命令 mvn clean package dockerfile:build -Dmaven.test.skip=true 编译项目并构建docker镜像，编译结束自动推送镜像到docker主机中。
	
	#docker 镜像制作
	#打包
	mvn clean package -Dmaven.test.skip=true
	#启动
	java -jar target/xxxx.jar
	#访问
	http://localhost:5084
	#上传镜像
	mvn clean package dockerfile:build -Dmaven.test.skip=true
#回到centos8中，docker下查看镜像
	#查看上传镜像命令：
	docker images|grep xxx	
	#启动容器，然后访问验证结果
	docker run -itd --name ContainerName -p docker容器外绑定端口:docker中端口         REPOSITORY：TAG  /bin/bash
	实例如下：docker run -itd --name test -p 5084:5084  demo/springboot-demo:latest  /bin/bash

	