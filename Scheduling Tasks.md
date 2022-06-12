# Scheduling Tasks

This guide walks you through the steps for scheduling tasks with Spring.

# What You Will Build

You will build an application that prints out the current time every five seconds by using Spring’s `@Scheduled` annotation.

## What You Need

- About 15 minutes
- A favorite text editor or IDE
- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or later
- [Gradle 4+](http://www.gradle.org/downloads) or [Maven 3.2+](https://maven.apache.org/download.cgi)
- You can also import the code straight into your IDE:
  - [Spring Tool Suite (STS)](https://spring.io/guides/gs/sts)
  - [IntelliJ IDEA](https://spring.io/guides/gs/intellij-idea/)

## How to complete this guide

Like most Spring [Getting Started guides](https://spring.io/guides), you can start from scratch and complete each step or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Starting with Spring Initializr](https://spring.io/guides/gs/scheduling-tasks/#scratch).

To **skip the basics**, do the following:

- [Download](https://github.com/spring-guides/gs-scheduling-tasks/archive/main.zip) and unzip the source repository for this guide, or clone it using [Git](https://spring.io/understanding/Git): `git clone https://github.com/spring-guides/gs-scheduling-tasks.git`
- cd into `gs-scheduling-tasks/initial`
- Jump ahead to [Create a Scheduled Task](https://spring.io/guides/gs/scheduling-tasks/#initial).

**When you finish**, you can check your results against the code in `gs-scheduling-tasks/complete`.

## Starting with Spring Initializr

You can use this [pre-initialized project](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=scheduling-tasks&name=scheduling-tasks&description=Demo project for Spring Boot&packageName=com.example.scheduling-tasks) and click Generate to download a ZIP file. This project is configured to fit the examples in this tutorial.

To manually initialize the project:

1. Navigate to [https://start.spring.io](https://start.spring.io/). This service pulls in all the dependencies you need for an application and does most of the setup for you.
2. Choose either Gradle or Maven and the language you want to use. This guide assumes that you chose Java.
3. Click **Generate**.
4. Download the resulting ZIP file, which is an archive of a web application that is configured with your choices.

|      | If your IDE has the Spring Initializr integration, you can complete this process from your IDE. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | You can also fork the project from Github and open it in your IDE or other editor. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

### Adding the `awaitility` Dependency

The tests in `complete/src/test/java/com/example/schedulingtasks/ScheduledTasksTest.java` require the `awaitility` library.

|      | Later versions of the `awaitility` library do not work for this test, so you have to specify version 3.1.2. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

To add the `awaitility` library to Maven, add the following dependency:

```
<dependency>
  <groupId>org.awaitility</groupId>
  <artifactId>awaitility</artifactId>
  <version>3.1.2</version>
  <scope>test</scope>
</dependency>COPY
```

The following listing shows the finished `pom.xml` file:

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.6.3</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>scheduling-tasks-complete</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>scheduling-tasks-complete</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.awaitility</groupId>
			<artifactId>awaitility</artifactId>
			<version>3.1.2</version>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>COPY
```

To add the `awaitility` library to Gradle, add the following dependency:

```
	testImplementation 'org.awaitility:awaitility:3.1.2'COPY
```

The following listing shows the finished `build.gradle` file:

```
plugins {
	id 'org.springframework.boot' version '2.6.3'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter'
	testImplementation 'org.awaitility:awaitility:3.1.2'
	testImplementation('org.springframework.boot:spring-boot-starter-test')
}

test {
	useJUnitPlatform()
}COPY
```

## Create a Scheduled Task

Now that you have set up your project, you can create a scheduled task. The following listing (from `src/main/java/com/example/schedulingtasks/ScheduledTasks.java`) shows how to do so:

```Java
/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.schedulingtasks;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 5000)
	public void reportCurrentTime() {
        //{}作用：用来显示时间
		log.info("The time is now {}", dateFormat.format(new Date()));
	}
}COPY
```

The `Scheduled` annotation defines when a particular method runs.

|      | This example uses `fixedRate`, which specifies the interval between method invocations, measured from the start time of each invocation. There are [other options](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html#scheduling-annotation-support-scheduled), such as `fixedDelay`, which specifies the interval between invocations measured from the completion of the task. You can also use [`@Scheduled(cron=". . .")`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html) expressions for more sophisticated task scheduling. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

## Enable Scheduling

Although scheduled tasks can be embedded in web applications and WAR files, the simpler approach (shown in the next listing) creates a standalone application. To do so, package everything in a single, executable JAR file, driven by a good old Java `main()` method. The following listing (from `src/main/java/com/example/schedulingtasks/SchedulingTasksApplication.java`) shows the application class:

```Java
package com.example.schedulingtasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulingTasksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingTasksApplication.class);
	}
}COPY
```

`@SpringBootApplication` is a convenience annotation that adds all of the following:

- `@Configuration`: Tags the class as a source of bean definitions for the application context.
- `@EnableAutoConfiguration`: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings. For example, if `spring-webmvc` is on the classpath, this annotation flags the application as a web application and activates key behaviors, such as setting up a `DispatcherServlet`.
- `@ComponentScan`: Tells Spring to look for other components, configurations, and services in the `com/example` package, letting it find the controllers.

The `main()` method uses Spring Boot’s `SpringApplication.run()` method to launch an application. Did you notice that there was not a single line of XML? There is no `web.xml` file, either. This web application is 100% pure Java and you did not have to deal with configuring any plumbing or infrastructure.

The [`@EnableScheduling`](https://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#scheduling-enable-annotation-support) annotation ensures that a background task executor is created. Without it, nothing gets scheduled.

### Build an executable JAR

You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

If you use Gradle, you can run the application by using `./gradlew bootRun`. Alternatively, you can build the JAR file by using `./gradlew build` and then run the JAR file, as follows:

```
java -jar build/libs/gs-scheduling-tasks-0.1.0.jar
```

If you use Maven, you can run the application by using `./mvnw spring-boot:run`. Alternatively, you can build the JAR file with `./mvnw clean package` and then run the JAR file, as follows:

```
java -jar target/gs-scheduling-tasks-0.1.0.jar
```

|      | The steps described here create a runnable JAR. You can also [build a classic WAR file](https://spring.io/guides/gs/convert-jar-to-war/). |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

Logging output is displayed, and you can see from the logs that it is on a background thread. You should see your scheduled task fire every five seconds. The following listing shows typical output:

```
...
2019-10-02 12:07:35.659  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:35
2019-10-02 12:07:40.659  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:40
2019-10-02 12:07:45.659  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:45
2019-10-02 12:07:50.657  INFO 28617 --- [   scheduling-1] c.e.schedulingtasks.ScheduledTasks       : The time is now 12:07:50
...COPY
```

## Summary

1. 引入awaitility依赖
2. 在需要定时任务的方法上，添加@Scheduled注解，@fixedRate:固定时间，@fixedDely：固定延迟时间，还可使用cron表达式
3. 在主方法类上添加@EnableScheduling注解

## 