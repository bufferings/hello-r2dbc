# hello-r2dbc

```sh
❯ (cd db; docker-compose up -d)
Creating network "db_default" with the default driver
Creating db_db_1_d69d7879eaa0 ... done

❯ (cd demo-webflux-r2dbc; ./mvnw spring-boot:run)
(...)
2018-11-17 20:59:32.474  INFO 25588 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8080
2018-11-17 20:59:32.479  INFO 25588 --- [           main] c.e.d.DemoWebfluxR2dbcApplication        : Started DemoWebfluxR2dbcApplication in 2.047 seconds (JVM running for 5.796)

❯ curl localhost:8080/spi/select
San FranciscoSan FranciscoSan FranciscoSan Francisco
```
