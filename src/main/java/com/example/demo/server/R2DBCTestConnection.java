package com.example.demo.server;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import reactor.core.publisher.Mono;

public class R2DBCTestConnection {

    public static void main(String[] args) {
        PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration.builder()
                .host("database")
                .port(5432)
                .username("postgres")
                .password("My01pass")
                .database("ownersandpets")
                .build();

        ConnectionFactory connectionFactory = new PostgresqlConnectionFactory(configuration);

        Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection.createStatement("SELECT 1").execute()) // Note the change from
                                                                                             // flatMap to flatMapMany
                .flatMap(result -> result.map((row, rowMetadata) -> row.get(0)))
                .doOnTerminate(() -> System.out.println("Finished testing the connection."))
                .doOnNext(data -> System.out.println("Connection successful! Value: " + data))
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .blockLast();

    }
}
