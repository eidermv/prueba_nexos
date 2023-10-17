package com.example.tarjeta.modelo;

import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
@Setter
public abstract class Conexion {

    private SqlClient clientMy;

    private int port;
    private String host;
    private String db;
    private String user;
    private String pass;

    public abstract Mono<String> llamarSP(String sp, String data);

    public abstract Mono<String> ejecutarSQL(String sql, List<Tuple> valores);
}
