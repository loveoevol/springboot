# Consuming a RESTful Web Service

**说明**：这个应用程序的功能是将其他网站传来的json字符串转换相应的Java对象。

This guide walks you through the process of creating an application that consumes a RESTful web service.

# What You Will Build

You will build an application that uses Spring’s `RestTemplate` to retrieve a random Spring Boot quotation at https://quoters.apps.pcfone.io/api/random.（这个网址目前无法访问，我自己写了一个spring，用来返回json字符串）

# What You Need

- About 15 minutes
- A favorite text editor or IDE
- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or later
- [Gradle 4+](http://www.gradle.org/downloads) or [Maven 3.2+](https://maven.apache.org/download.cgi)
- You can also import the code straight into your IDE:
  - [Spring Tool Suite (STS)](https://spring.io/guides/gs/sts)
  - [IntelliJ IDEA](https://spring.io/guides/gs/intellij-idea/)

# How to complete this guide

Like most Spring [Getting Started guides](https://spring.io/guides), you can start from scratch and complete each step or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Starting with Spring Initializr](https://spring.io/guides/gs/consuming-rest/#scratch).

To **skip the basics**, do the following:

- [Download](https://github.com/spring-guides/gs-consuming-rest/archive/main.zip) and unzip the source repository for this guide, or clone it using [Git](https://spring.io/understanding/Git): `git clone https://github.com/spring-guides/gs-consuming-rest.git`
- cd into `gs-consuming-rest/initial`
- Jump ahead to [Fetching a REST Resource](https://spring.io/guides/gs/consuming-rest/#initial).

**When you finish**, you can check your results against the code in `gs-consuming-rest/complete`.

# Starting with Spring Initializr

You can use this [pre-initialized project](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=consuming-rest&name=consuming-rest&description=Demo project for Spring Boot&packageName=com.example.consuming-rest&dependencies=web) and click Generate to download a ZIP file. This project is configured to fit the examples in this tutorial.

To manually initialize the project:

1. Navigate to [https://start.spring.io](https://start.spring.io/). This service pulls in all the dependencies you need for an application and does most of the setup for you.
2. Choose either Gradle or Maven and the language you want to use. This guide assumes that you chose Java.
3. Click **Dependencies** and select **Spring Web**.
4. Click **Generate**.
5. Download the resulting ZIP file, which is an archive of a web application that is configured with your choices.

|      | If your IDE has the Spring Initializr integration, you can complete this process from your IDE. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

|      | You can also fork the project from Github and open it in your IDE or other editor. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

## Fetching a REST Resource

这里改为自己的网址，相应的war包，放在本文档同目录

With project setup complete, you can create a simple application that consumes a RESTful service.

A RESTful service has been stood up at http://localhost:8090/Sping-1.0-SNAPSHOT/get/json. It randomly fetches quotations about Spring Boot and returns them as JSON documents.

If you request that URL through a web browser or curl, you receive a JSON document that looks something like this:

```
{
   type: "success",
   value: {
      id: 10,
      quote: "Really loving Spring Boot, makes stand alone Spring apps easy."
   }
}COPY
```

That is easy enough but not terribly useful when fetched through a browser or through curl.

A more useful way to consume a REST web service is programmatically. To help you with that task, Spring provides a convenient template class called [`RestTemplate`](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html). `RestTemplate` makes interacting with most RESTful services a one-line incantation. And it can even bind that data to custom domain types.

First, you need to create a domain class to contain the data that you need. The following listing shows the `Quote` class, which you can use as your domain class:

```java
src/main/java/com/example/consumingrest/Quote.java
package com.example.consumingrest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {

  private String type;
  private Value value;

  public Quote() {
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Value getValue() {
    return value;
  }

  public void setValue(Value value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Quote{" +
        "type='" + type + '\'' +
        ", value=" + value +
        '}';
  }
}
```

**@JsonIgnoreProperties：对没有绑定的属性忽略**

This simple Java class has a handful of properties and matching getter methods. It is annotated with `@JsonIgnoreProperties` from the Jackson JSON processing library to indicate that any properties not bound in this type should be ignored.

**自定义的类型的属性名应该和json中对应的键名一直，若不一致可使用@JsonProperty**

To directly bind your data to your custom types, you need to specify the variable name to be exactly the same as the key in the JSON document returned from the API. In case your variable name and key in JSON doc do not match, you can use `@JsonProperty` annotation to specify the exact key of the JSON document. (This example matches each variable name to a JSON key, so you do not need that annotation here.)

You also need an additional class, to embed the inner quotation itself. The `Value` class fills that need and is shown in the following listing (at `src/main/java/com/example/consumingrest/Value.java`):

```
package com.example.consumingrest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Value {

  private Long id;
  private String quote;

  public Value() {
  }

  public Long getId() {
    return this.id;
  }

  public String getQuote() {
    return this.quote;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setQuote(String quote) {
    this.quote = quote;
  }

  @Override
  public String toString() {
    return "Value{" +
        "id=" + id +
        ", quote='" + quote + '\'' +
        '}';
  }
}COPY
```

This uses the same annotations but maps onto other data fields.

# Finishing the Application

The Initalizr creates a class with a `main()` method. The following listing shows the class the Initializr creates (at `src/main/java/com/example/consumingrest/ConsumingRestApplication.java`):

```
package com.example.consumingrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumingRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumingRestApplication.class, args);
	}

}COPY
```

Now you need to add a few other things to the `ConsumingRestApplication` class to get it to show quotations from our RESTful source. You need to add:

- A logger, to send output to the log (the console, in this example).
- A `RestTemplate`, which uses the Jackson JSON processing library to process the incoming data.
- A `CommandLineRunner` that runs the `RestTemplate` (and, consequently, fetches our quotation) on startup.

The following listing shows the finished `ConsumingRestApplication` class (at `src/main/java/com/example/consumingrest/ConsumingRestApplication.java`):

```java
package com.example.consumingrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ConsumingRestApplication {

	private static final Logger log = LoggerFactory.getLogger(ConsumingRestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumingRestApplication.class, args);
	}
	//这个bean原文档没有
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			Quote quote = restTemplate.getForObject(
					"https://quoters.apps.pcfone.io/api/random", Quote.class);
			log.info(quote.toString());
		};
	}
}
```

# Running the Application

You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

If you use Gradle, you can run the application by using `./gradlew bootRun`. Alternatively, you can build the JAR file by using `./gradlew build` and then run the JAR file, as follows:

```
java -jar build/libs/gs-consuming-rest-0.1.0.jar
```

If you use Maven, you can run the application by using `./mvnw spring-boot:run`. Alternatively, you can build the JAR file with `./mvnw clean package` and then run the JAR file, as follows:

```
java -jar target/gs-consuming-rest-0.1.0.jar
```

|      | The steps described here create a runnable JAR. You can also [build a classic WAR file](https://spring.io/guides/gs/convert-jar-to-war/). |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

You should see output similar to the following but with a random quotation:

```
2019-08-22 14:06:46.506  INFO 42940 --- [           main] c.e.c.ConsumingRestApplication           : Quote{type='success', value=Value{id=1, quote='Working with Spring Boot is like pair-programming with the Spring developers.'}}
```

|      | If you see an error that reads, `Could not extract response: no suitable HttpMessageConverter found for response type [class com.example.consumingrest.Quote]`, it is possible that you are in an environment that cannot connect to the backend service (which sends JSON if you can reach it). Maybe you are behind a corporate proxy. Try setting the `http.proxyHost` and `http.proxyPort` system properties to values appropriate for your environment. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

# Summary

Congratulations! You have just developed a simple REST client by using Spring Boot.