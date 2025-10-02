package com.surrogate.Zoolip.models.login;



import com.surrogate.Zoolip.models.peticiones.Response;
import lombok.Getter;

import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class LoginResponse extends Response {

    public LoginResponse(String status, String token, String username, Long id) {
        super(status, token, username, id);
    }
    public LoginResponse(String status, String httpError, String response) {
        super(status, httpError, response);
    }

    public LoginResponse(String status) {
        super(status);
    }

    public LoginResponse(String status, String message) {
        super(status, message);
    }
}




