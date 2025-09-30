package com.surrogate.Zoolip.models.register;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.surrogate.Zoolip.models.peticiones.Request;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RegisterRequest extends Request {

    private String rol;
    private String username;

    private String password;

    @JsonCreator
    public RegisterRequest(@JsonProperty("username") String username,
                           @JsonProperty("password") String password,
                           @JsonProperty("rol") String rol
                          ) {

        this.username = username;
        this.password = password;
        this.rol = rol;

    }

    public RegisterRequest() {
    }

}
