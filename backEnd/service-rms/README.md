#Fork 早上好呀

//222test1111333111
#test111
#多数据源数据库适配说明
@DS("XX") 注释
1.默认配置中 @DS("mysql") 可以省略掉；
2.方法上的@DS(XX)配置，高于类中上的配置

#版本控制
		<java.version>1.8</java.version>
		<swagger.version>2.8.0</swagger.version>
		<mybatis-plus.version>3.1.0</mybatis-plus.version>
		<dynamic-datasource.version>3.2.0</dynamic-datasource.version>
		<spring.boot.admin.version>2.1.5</spring.boot.admin.version>
		<springCloud.verison>Greenwich.SR2</springCloud.verison>
		<springBoot.verison>2.1.15.RELEASE</springBoot.verison>
#参考网址：https://baomidou.com/guide/dynamic-datasource.html	
#web容器启动插件
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
#实体简写插件
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>

# 数据库连接配置	

		<!--mybatis-plus -->
		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>mybatis-plus-boot-starter</artifactId>
		</dependency>	
		<!--mysql 连接插件 -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<!-- Sring.datasource.type 赋值转换为连接池对象 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.1.6</version>
		</dependency>
#阿里数据库连接池
		<!-- 引入阿里数据库连接池 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.1.6</version>
		</dependency>
备注：采用多数据库插件时，该插件已经被集成，故此可以不用引入
#多数据库连接插件
		<!-- 动态数据库插件 -->
		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>dynamic-datasource-spring-boot-starter</artifactId>
			<version>${dynamic-datasource.version}</version>
		</dependency>
		
#实体创建数据库表插件		
		<!-- 最新实体动态表创建 -->
		<dependency>
			<groupId>com.gitee.sunchenbin.mybatis.actable</groupId>
			<artifactId>mybatis-enhance-actable</artifactId>
			<version>1.2.1.RELEASE</version>			
		</dependency>
#注册插件
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-consul-discovery</artifactId>
		</dependency>
#客户端监测插件
		<dependency>
			<groupId>de.codecentric</groupId>
			<artifactId>spring-boot-admin-starter-client</artifactId>
			<version>${spring.boot.admin.version}</version>
		</dependency>

#热部署插件
         <!-- 热部署 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
#多数据源，插件冲突
	     <!-- 动态数据库插件 -->
		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>dynamic-datasource-spring-boot-starter</artifactId>
			<version>${dynamic-datasource.version}</version>
		</dependency>
备注：该插件与springboot中 DataSourceAutoConfiguration.class冲突，在选择时自动采用其中一种，本demo中，提供原生的，两种数据源的切换方式--简单维护容易。
#Cloud与springBoot版本

    <dependencyManagement>              
    	<dependencies>		
    		<!-- Cloud的版本 -->			
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-dependencies</artifactId>
    			<version>${springCloud.verison}</version>
    			<type>pom</type>
    			<scope>import</scope>
    		</dependency>			
    		<!-- Boot版本 -->
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-dependencies</artifactId>
    			<version>${springBoot.verison}</version>
    			<type>pom</type>
    			<scope>import</scope>
    		</dependency>
    	</dependencies>		
    </dependencyManagement>
#备注(Cloud与springBoot版本)：
采用该方式，可以在springboot中集成的插件中省略版本号，便于版本的统一管理)	

#参考网址：https://gitee.com/catoop/springboot-docker
# pom.xml  中dockerfile-maven-plugin插件配置

引用插件

	<dependency>
	    <groupId>com.spotify</groupId>
	    <artifactId>dockerfile-maven-plugin</artifactId>
	    <version>1.4.13</version>
	</dependency>
	
	配置属性中配置
	<properties>
		<docker.image.prefix>demo</docker.image.prefix>
	</properties>
	插件引用方式如下：
	<plugin>
		<groupId>com.spotify</groupId>
		<artifactId>dockerfile-maven-plugin</artifactId>
		<version>1.4.3</version>
		<configuration>
			<repository>${docker.image.prefix}/${project.artifactId}</repository>
				<!-- <repository>test</repository> -->
				<buildArgs>
					<JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
				</buildArgs>
		</configuration>
	</plugin>
#日志插件Lombok
	注解@Slf4j的使用，
	<dependency>
	   <groupId>org.projectlombok</groupId>
	   <artifactId>lombok</artifactId>
	    <version>xxx</version><!--版本号自己选一个就行-->
	</dependency>
备注：该版本号，依赖springboot中的版本，且springboot2.1.x中已经实现了日志的实现
	
