package com.example.tarjeta.servicio;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface Servicio<T> {

    public Mono<ResponseEntity> consultar(Integer datos);

    public Mono<ResponseEntity> listar();

    public Mono<ResponseEntity> crear(T datos);

    public Mono<ResponseEntity> actualizar(T datos);

    public Mono<ResponseEntity> eliminar(String datos);

}
