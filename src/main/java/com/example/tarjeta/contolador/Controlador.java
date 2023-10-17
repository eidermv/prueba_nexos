package com.example.tarjeta.contolador;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;


public interface Controlador<T> {

    public Mono<ResponseEntity> consultar(String datos);

    public Mono<ResponseEntity> listar(String datos);

    public Mono<ResponseEntity> crear(T datos);

    public Mono<ResponseEntity> actualizar(T datos);

    public Mono<ResponseEntity> eliminar(String datos);
}
