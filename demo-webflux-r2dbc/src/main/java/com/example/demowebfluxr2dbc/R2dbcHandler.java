package com.example.demowebfluxr2dbc;

import io.r2dbc.client.R2dbc;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class R2dbcHandler {

  private R2dbc r2dbc;

  R2dbcHandler(PostgresqlConnectionFactory postgresqlConnectionFactory) {
    this.r2dbc = new R2dbc(postgresqlConnectionFactory);
  }

  RouterFunction<ServerResponse> routes() {
    return route(GET("/r2dbc/select"), req -> ok().body(select(), String.class))
        .andRoute(GET("/r2dbc/select-in-tx"), req -> ok().body(selectInTx(), String.class));
  }

  private Flux<String> select() {
    return r2dbc.withHandle(h ->
        h.createQuery("SELECT city FROM weather")
            .mapRow((row, metadata) -> row.get("city", String.class)));
  }

  private Flux<String> selectInTx() {
    return r2dbc.inTransaction(h ->
        h.select("SELECT city, temp_lo, temp_hi FROM weather")
            .mapRow(row -> row.get("city", String.class)));
  }
}
