package com.example.tarjeta.dto;

import io.vertx.core.json.JsonObject;

public class RespuestaDto {

    private String mensaje;

    public RespuestaDto(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject().put("mensaje", this.mensaje);
        return object.toString();
    }
}
