本 repo 是阅读这里的实践: https://dzone.com/articles/microservices-using-spring-boot-amp-spring-cloud-p

区别只是使用了最新的 spring cloud 版本 (spring boot 2.0.6 + spring cloud Finchley.SR2)

## Spring cloud 各组件的作用

**Spring Cloud** is essentially an implementation of various design patterns to be followed while building Cloud Native applications. Instead of reinventing the wheel, we can simply take advantage of various Spring Cloud modules and focus on our main business problem rather than worrying about infrastructural concerns.

Following are just a few Spring Cloud modules that can be used to address distributed application concerns:

1. **Spring Cloud Config Server:** Used to externalize the configuration of applications in a central config server with the ability to update the configuration values without requiring to restart the applications. We can use Spring Cloud Config Server with **git**, **Consul**, or **ZooKeeper** as a config repository.

2. **Service Registry and Discovery:** As there could be many services and we need the ability to scale up or down dynamically, we need a Service Registry and Discovery mechanism so that service-to-service communication does not depend on hard-coded hostnames and port numbers. Spring Cloud provides **Netflix Eureka**-based Service Registry and Discovery support with just minimal configuration. We can also use **Consul** or **ZooKeeper** for Service Registry and Discovery.

3. **Circuit Breaker:** In microservices-based architecture, one service might depend on another service, and if one service goes down, then failures may cascade to other services as well. Spring Cloud provides a Netflix Hystrix-based Circuit Breaker to handle these kinds of issues.

4. **Spring Cloud Data Streams:** We may need to work with huge volumes of data streams using **Kafka** or **Spark**. Spring Cloud Data Streams provides higher-level abstractions to use those frameworks more easily.

5. **Spring Cloud Security:** Some microservices need to be accessible to authenticated users only, and most likely, we'll want a **Single Sign-On** feature to propagate the authentication context across services. Spring Cloud Security provides authentication services using OAuth2.

6. **Distributed Tracing:** One of the pain points with microservices is the ability to debug issues. One simple end-user action might trigger a chain of microservice calls; there should be a mechanism to trace the related call chains. We can use **Spring Cloud Sleuth with Zipkin** to trace cross-service invocations.

7. **Spring Cloud Contract:** There is a high chance that separate teams will work on different microservices. There should be a mechanism for teams to agree upon API endpoint contracts so that each team can develop their APIs independently. **Spring Cloud Contract** helps to create such contracts and validate them by both the service provider and consumer.

[Zuul 架构图](https://dzone.com/articles/microservices-communication-zuul-api-gateway-1)

## Cyper 的笔记

1. spring cloud 中的任意一个项目都是一个独立的 spring boot application. 你需要某个 feature? 好的, 通过 start.spring.io 建一个有对应 feature 的 spring boot 项目.
2. 套路: start 上生成 zip 文件, 解压, 复制解压的目录到 eclipse, 然后 import from existing maven project.
3. 在主类 xxxApplication 上加或不加注解, 在 application.properties 或 bootstrap.properties 稍微做一下配置. 就这些.

config server/client/vault 都不是很有必要.
eureka 还挺有用, 但是 k8s 也是做这个事的, 是不是功能上有所重叠?

### Config Server

1. 项目名称: config-server(8888)
2. 模板: Config Server
3. 注解: @EnableConfigServer
4. 配置 application.properties

```
spring.config.name=configserver
server.port=8888

spring.cloud.config.server.git.uri=https://github.com/uniquejava-demos/microservices-config-repo
spring.cloud.config.server.git.clone-on-start=true

management.endpoints.web.exposure.include=*
```

### Config Client

1. 项目名称: catalog-service, inventory-service 等
2. 模板: Config Client
3. 注解: 无需注解
4. 配置 bootstrap.properties

```
spring.cloud.config.uri=http://localhost:8888
```

然后把项目所需要的配置建成<spring-application-name>.properties 扔到 git config-repo 上(或使用本地文件系统, 没试!).

### Config Client + Vault(Optional)

1. 项目名称: catalog-service, inventory-service 等
2. 模板: Config Client + Vault Configuration
3. 注解: 无需注解
4. 配置 bootstrap.properties

```
spring.cloud.vault.host=localhost
spring.cloud.vault.port=8200
spring.cloud.vault.scheme=http
spring.cloud.vault.authentication=token
spring.cloud.vault.token=xxxxx
spring.cloud.vault.generic.backend=my-app
```

需要启动 vault server 然后通过`vault write my-app/catalog-service @catalog-service-credentials.json`将对应的 credentials 事先 put 到 vault.

### Eureka Server

1. 项目名称: service-registry(8671)
2. 模板: Eureka Server
3. 注解: @EnableEurekaServer
4. 配置 application.properties

```
eureka.instance.hostname=localhost
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.instance.lease-renewal-interval-in-seconds=30
eureka.instance.prefer-ip-address=true
```

Eureka Server 也带 Eureka client 功能(为了实现 Eureka server 高可用, 可以配置多个 node)

控制台: http://localhost:8761

### Eureka Discovery

1. 项目名称: catalog-service, inventory-service, shoppingcart-ui(也是 Zuul Proxy)
2. 模板: Eureka Discovery
3. 注解: 无需注解
4. 配置 application.properties

```
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### RestTemplate 实现 Service 间相互调用

1. 项目名称: catalog-service
2. 模板: 无
3. 注解: 无

需要注入的 Bean

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}
```

RestTemplate 用法

```java
ResponseEntity<ProductInventoryResponse> itemResEntity
  = restTemplate.getForEntity(
     "http://inventory-service/api/inventory/{code}", ProductInventoryResponse.class, productCode)
```

### Hystrix(Circuit Breaker)

1. 项目名称: catalog-service
2. 模板: Hystrix
3. 注解: @EnableCircuitBreaker, @HystrixCommand(fallbackMethod = "getDefaultProductInventoryByCode")
4. 配置 application.properties(可选)

```
hystrix.command.getProductInventoryByCode.execution.isolation.thread.timeoutInMilliseconds=2000
hystrix.command.getProductInventoryByCode.circuitBreaker.errorThresholdPercentage=60
```

让 ProductService 通过 InventoryServiceClient(使用了@HystrixCommand 注解) 而非 RestTemplate 调用 inventory service.

Hystrix stream: http://localhost:8181/actuator/hystrix.stream

Hystrix Dashboard(未做实验)

### Zuul

1. 项目名称: shoppingcart-ui
2. 模板: Zuul
3. 注解: @EnableZuulProxy
4. 配置 application.properties(可选)

```
zuul.proxy=/api
zuul.routes.catalogservice.path=/catalog/**
zuul.routes.catalogservice.serviceId=catalog-service
zuul.routes.orderservice.path=/orders/**
zuul.routes.orderservice.serviceId=order-service
```

ZuulFilter 的用法(这个是抽象类而非接口)

声明一个 Bean 即可.

```java
@Bean
AuthHeaderFilter authHeaderFilter() {
	return new AuthHeaderFilter();
}
```

## 踩到的坑

### vault

vault 的问题: https://stackoverflow.com/questions/49872480/vault-error-while-writing

startup

```sh
➜  vault server -dev

➜  vault secrets enable -path=my-app kv
Success! Enabled the kv secrets engine at: my-app/
➜  vault write my-app/my-app password=123
Success! Data written to: my-app/my-app
➜  vault read my-app/my-app
Key                 Value
---                 -----
refresh_interval    768h
password            123
➜  vault write my-app/catalog-service @catalog-service-credentials.json
Success! Data written to: my-app/catalog-service
vault write my-app/catalog-service @catalog-service-credentials.json
```

### Eureka client 启动并不报错但是注册不到 eureka server?

我把 pom 写错, 结果怎么配置都没用...

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-netflix-eureka-client</artifactId>
</dependency>
```

正确的写法.

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 启动 service 时 spring.datasource.username 和 password 都为 null

检查项:

1. vault 的`myapp/application-name`之下有无对应的 key.
2. config-repo 中 有无对应的 application-name.properties 文件.
3. inventory-service 需要的 database 是否已创建.

### 新增一个全功能的 service 要添加的依赖列表

要添加的服务 web, jpa, mysql, config-client, eureka-discovery, lombok, vault

在 git repo 上要添加 inventory-service.properties

要把 credentials 加到 vault 对应的 key 下, `vault write my-app/inventory-service @inventory-service-credentials.json`

### Zuul Proxy/API Gateway/Edge Service

Enable 后访问

> curl http://localhost:8181/api/products/P002

等价于访问

> curl http://localhost:8080/catalog-service/api/products/P002

他们会返回相同的结果

ZuulFilter 中设置的 header 会 pass 到 downstream service..但是 downstream service 通过 RestTemplate 调用 downdownstream service 的时候 header 信息会丢失.

可以使用 restTempate 的 exchange 方法

相关 QA: https://stackoverflow.com/questions/10358345/making-authenticated-post-requests-with-spring-resttemplate-for-android
