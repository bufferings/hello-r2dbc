package com.example.demowebfluxr2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.data.domain.Sort.Order.desc;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class SpringDataR2dbcHandler {

  private DatabaseClient db;

  SpringDataR2dbcHandler(PostgresqlConnectionFactory postgresqlConnectionFactory) {
    this.db = DatabaseClient.create(postgresqlConnectionFactory);
  }

  RouterFunction<ServerResponse> routes() {
    return route(GET("/spring-data/generic-insert"), req -> ok().body(genericInsert(), Integer.class))
        .andRoute(GET("/spring-data/generic-select1"), req -> ok().body(genericSelect1().map(Object::toString), String.class))
        .andRoute(GET("/spring-data/generic-select2"), req -> ok().body(genericSelect2(), String.class))
        .andRoute(GET("/spring-data/insert"), req -> ok().body(insert(), String.class))
        .andRoute(GET("/spring-data/insert-with-dto"), req -> ok().body(insertWithDto(), String.class))
        .andRoute(GET("/spring-data/select1"), req -> ok().body(select1(), new ParameterizedTypeReference<Map<String, Object>>() {
        }))
        .andRoute(GET("/spring-data/select2"), req -> ok().body(select2(), Weather.class));
  }


  private Mono<Integer> genericInsert() {
    Mono<Integer> count = db.execute()
        .sql("INSERT INTO weather (city, temp_lo, temp_hi) VALUES($1, $2, $3)")
        .bind("$1", "Osaka")
        .bind("$2", 5)
        .bind("$3", 18)
        .fetch()
        .rowsUpdated();
    return count;
  }

  private Flux<Map<String, Object>> genericSelect1() {
    Flux<Map<String, Object>> result = db.execute()
        .sql("SELECT city, lo_temp FROM weather")
        .fetch()
        .all();
    return result;
  }

  private Flux<String> genericSelect2() {
    Flux<String> result = db.execute()
        .sql("SELECT city FROM weather")
        .exchange()
        .flatMapMany(it -> it.extract((r, md) -> r.get(0, String.class)).all());
    return result;
  }

  private Flux<String> insert() {
    Flux<String> cities = db.insert()
        .into("weather")
        .value("city", "Osaka2")
        .value("temp_lo", 10)
        .value("temp_hi", 13)
        .exchange()
        .flatMapMany(it -> it.extract((r, md) ->
            "city:" + r.get("city", String.class) +
                " temp_lo:" + r.get("temp_lo", Integer.class)).all());
    return cities;
  }

  private Flux<String> insertWithDto() {
    Weather weather = Weather.create("Kyoto", 1, 30);
    Flux<String> cities = db.insert()
        .into(Weather.class)
        .using(weather)
        .exchange()
        .flatMapMany(it -> it.extract((r, md) ->
            "city:" + r.get("city", String.class)).all());
    return cities;
  }

  private Flux<Map<String, Object>> select1() {
    Flux<Map<String, Object>> rows = db.select()
        .from("weather")
        .orderBy(Sort.by(desc("temp_lo")))
        .fetch()
        .all();
    return rows;
  }

  private Flux<Weather> select2() {
    Flux<Weather> rows = db.select()
        .from(Weather.class)
        .orderBy(Sort.by(desc("temp_lo")))
        .fetch()
        .all();
    return rows;
  }

  @Table("weather")
  public static class Weather {

    static Weather create(String city, Integer tempLo, Integer tempHi) {
      Weather weather = new Weather();
      weather.city = city;
      weather.tempLo = tempLo;
      weather.tempHi = tempHi;
      return weather;
    }

    public String city;
    @Column("temp_lo")
    public Integer tempLo;
    @Column("temp_hi")
    public Integer tempHi;
  }

}
