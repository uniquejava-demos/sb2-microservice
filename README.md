### vault

vault的问题: https://stackoverflow.com/questions/49872480/vault-error-while-writing

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

catalog-service/bootstrap.properties配置

```
spring.cloud.vault.host=localhost
spring.cloud.vault.port=8200
spring.cloud.vault.scheme=http
spring.cloud.vault.authentication=token
spring.cloud.vault.token=xxxxxxxxxxxxxxx
spring.cloud.vault.generic.backend=my-app
```

### eureka client
我把pom写错, 结果怎么配置都没用...

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-netflix-eureka-client</artifactId>
</dependency>
```

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 新增inventory-service
要添加的服务web, jpa, mysql, config-client, eureka-discovery, lombok, vault

在git repo上要添加inventory-service.properties

要把credentials加到vault对应的key下, `vault write my-app/inventory-service @inventory-service-credentials.json`


### 给catalog-service添加hystrix(circuit-breaker)
让ProductService通过InventoryServiceClient而非RestTemplate调用inventory service.

http://localhost:8181/actuator/hystrix.stream


