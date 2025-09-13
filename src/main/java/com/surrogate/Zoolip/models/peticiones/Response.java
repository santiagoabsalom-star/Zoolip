package com.surrogate.Zoolip.models.peticiones;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private String status;
    private String token;
    private String message;
    private Long id;
    private String httpError;
    private String username;

    public Response(String status, String token, String username, Long id) {
        this.username= username;
        this.status = status;
        this.token = token;
        this.id = id;
    }
    public Response(){
    }
    public Response(String status, String token, Long id) {
        this.status = status;
        this.token = token;
        this.id = id;
    }

    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }
     
    public Response(String status, String numero, String message, Long id, String username) {
        this.id=id;
        this.username=username;
        this.status = status;
        this.httpError = numero;
        this.message = message;


    }

    public Response(String status, String numero, String message) {
        this.status = status;
        this.httpError = numero;
        this.message = message;


    }

}
