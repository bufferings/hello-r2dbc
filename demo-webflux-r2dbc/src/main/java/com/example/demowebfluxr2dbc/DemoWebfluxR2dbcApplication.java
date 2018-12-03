package com.example.demowebfluxr2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootApplication
public class DemoWebfluxR2dbcApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoWebfluxR2dbcApplication.class, args);
  }

  @Bean
  RouterFunction<ServerResponse> routes(SpiHandler spiHandler,
                                        R2dbcHandler r2dbcHandler,
                                        SpringDataR2dbcHandler springDataR2dbcHandler) {
    return spiHandler.routes()
        .and(r2dbcHandler.routes())
        .and(springDataR2dbcHandler.routes());
  }

  @Bean
  PostgresqlConnectionFactory postgresqlConnectionFactory() {
    var configuration = PostgresqlConnectionConfiguration.builder()
        .host("localhost")
        .database("postgres")
        .username("postgres")
        .password("mysecretpassword")
        .build();
    return new PostgresqlConnectionFactory(configuration);
  }

}
