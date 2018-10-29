### vault

vault的问题: https://stackoverflow.com/questions/49872480/vault-error-while-writing

startup

```sh
➜  vault server -dev

➜  tmp1 vault secrets enable -path=my-app kv
Success! Enabled the kv secrets engine at: my-app/
➜  tmp1 vault write my-app/my-app password=123
Success! Data written to: my-app/my-app
➜  tmp1 vault read my-app/my-app
Key                 Value
---                 -----
refresh_interval    768h
password            123
➜  tmp1 vault write my-app/catalog-service @catalog-service-credentials.json
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