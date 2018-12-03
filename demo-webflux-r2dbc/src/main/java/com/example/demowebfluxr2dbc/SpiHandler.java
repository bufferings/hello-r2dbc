package com.example.demowebfluxr2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class SpiHandler {

  private PostgresqlConnectionFactory postgresqlConnectionFactory;

  SpiHandler(PostgresqlConnectionFactory postgresqlConnectionFactory) {
    this.postgresqlConnectionFactory = postgresqlConnectionFactory;
  }

  RouterFunction<ServerResponse> routes() {
    return route(GET("/spi/select"), req -> ok().body(select(), String.class));
  }

  private Flux<String> select() {
    return postgresqlConnectionFactory.create()
        .flatMapMany(connection ->
            connection.createStatement("SELECT city FROM weather")
                .execute()
                .flatMap(result ->
                    result.map((row, metadata) ->
                        row.get("city", String.class)
                    )
                )
        );
  }
}
