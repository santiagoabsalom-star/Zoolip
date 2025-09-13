package com.surrogate.Zoolip.models.register;




import com.surrogate.Zoolip.models.peticiones.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)

public class RegisterResponse extends Response {
    public RegisterResponse(String message, String status) {
        super(message, status);

    }

public RegisterResponse(){

}
}

