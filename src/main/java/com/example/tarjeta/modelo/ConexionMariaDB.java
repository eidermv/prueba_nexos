package com.example.tarjeta.modelo;

import io.vertx.core.json.JsonArray;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
@Component
public class ConexionMariaDB extends Conexion {

    @Autowired
    public ConexionMariaDB(
            @Value("${mariadb.port}") int port,
            @Value("${mariadb.host}") String host,
            @Value("${mariadb.db}") String db,
            @Value("${mariadb.user}") String user,
            @Value("${mariadb.pass}") String pass
    ) {
        this.setPort(port);
        this.setHost(host);
        this.setDb(db);
        this.setUser(user);
        this.setPass(pass);

        System.out.println("USER -> " + this.getUser());
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(this.getPort())
                .setHost(this.getHost())
                .setDatabase(this.getDb())
                .setUser(this.getUser())
                .setPassword(this.getPass());
                //.setReconnectAttempts(2)
                //.setReconnectInterval(1000);

// Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5)
                .setIdleTimeout(60);

// Create the pooled client
        this.setClientMy(PgPool.pool(connectOptions, poolOptions));
    }

    public Mono<String> llamarSP(String sp, String data) {
        //System.out.println("______________________ -> " + sp);
        return Mono.fromDirect(call -> {

            String sql =   "begin not atomic\n" +
                    "declare rta LONGTEXT default '';\n" +
                    "call "+sp+"('"+data+"',rta);\n" +
                    "select rta;\n" +
                    "end";

            //System.out.println("__________________________________SQL");
            //System.out.println(sql);
            //System.out.println("__________________________________FIN SQL");

            this.getClientMy().query(sql).execute(rowSetAsyncResult -> {
                if (rowSetAsyncResult.succeeded()) {
                    // handle the result
                    RowSet<Row> result1 = rowSetAsyncResult.result();
                    Row row1 = result1.iterator().next();
                    System.out.println("First result: " + row1.getString(0));

                    call.onNext(row1.getString(0));
                    call.onComplete();
                } else {
                    System.out.println("Failure: " + rowSetAsyncResult.cause().getMessage());
                    call.onNext(rowSetAsyncResult.cause().getMessage());
                    call.onComplete();
                }
            });
        });
    }

    @Override
    public Mono<String> ejecutarSQL(String sql, List<Tuple> valores) {
        return Mono.fromDirect(call -> {

            // "SELECT * FROM users WHERE id=$1"

            // List<Tuple> batch = new ArrayList<>();
            // batch.add(Tuple.tuple());


            this.getClientMy()
                    .preparedQuery(sql)
                    .executeBatch(valores, ar -> {
                        Respuesta respuesta;
                        if (ar.succeeded()) {
                            RowSet<Row> rows = ar.result();
                            if (rows.size()>0) {
                                JsonArray jsonArray = new JsonArray();
                                for (Row row : rows) {
                                    jsonArray.add(row.toJson());
                                    System.out.println(row.toJson());
                                }
                                respuesta = new Respuesta(0, "Exito", jsonArray);
                                call.onNext(respuesta.toString());
                                call.onComplete();
                            } else {
                                System.out.println(rows.size());

                                respuesta = new Respuesta(0, "Exito");
                                call.onNext(respuesta.toString());
                                call.onComplete();
                            }
                            //System.out.println("Got " + rows.size() + " rows ");
                        } else {
                            System.out.println("Failure: " + ar.cause().getMessage());
                            respuesta = new Respuesta(-1, ar.cause().getMessage());
                            call.onNext(respuesta.toString());
                            call.onComplete();
                        }
                    });

        });
    }
}
