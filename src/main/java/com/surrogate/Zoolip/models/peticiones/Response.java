package com.surrogate.Zoolip.models.peticiones;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Response {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String httpError;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;

    public Response(String status, String token, String username, Long id) {
        this.username= username;
        this.status = status;
        this.token = token;
        this.id = id;
    }
    public Response(){
    }
    public Response(String status) {
        this.status = status;
    }


    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }
     


    public Response(String status, String numero, String message) {
        this.status = status;
        this.httpError = numero;
        this.message = message;


    }

}
