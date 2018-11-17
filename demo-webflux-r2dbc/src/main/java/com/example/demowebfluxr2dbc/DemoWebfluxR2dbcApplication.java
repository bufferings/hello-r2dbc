package com.example.demowebfluxr2dbc;

import io.r2dbc.client.R2dbc;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class DemoWebfluxR2dbcApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoWebfluxR2dbcApplication.class, args);
  }

  @Bean
  RouterFunction<ServerResponse> getRoute() {
    return route(GET("/"),
        req -> ok().body(hello(), String.class));
  }

  private Flux<String> hello() {
    var connectionFactory = getPostgresqlConnectionFactory();
    var r2dbc = new R2dbc(connectionFactory);
    return r2dbc.inTransaction(h ->
        h.select("SELECT city, temp_lo, temp_hi, prcp, date FROM weather")
            .mapRow(row -> row.get("city", String.class)));
  }

  private PostgresqlConnectionFactory getPostgresqlConnectionFactory() {
    var configuration = PostgresqlConnectionConfiguration.builder()
        .host("localhost")
        .database("postgres")
        .username("postgres")
        .password("mysecretpassword")
        .build();
    return new PostgresqlConnectionFactory(configuration);
  }
}
