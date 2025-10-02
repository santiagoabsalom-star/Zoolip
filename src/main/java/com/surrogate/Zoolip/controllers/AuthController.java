package com.surrogate.Zoolip.controllers;

import com.surrogate.Zoolip.models.login.LoginRequest;
import com.surrogate.Zoolip.models.login.LoginResponse;
import com.surrogate.Zoolip.models.register.RegisterRequest;
import com.surrogate.Zoolip.models.register.RegisterResponse;
import com.surrogate.Zoolip.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);

            if ("success".equals(response.getStatus())) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + response.getToken())
                        .header("Id-Usuario", String.valueOf(response.getId()))
                        .header("Nombre-Usuario", response.getUsername())
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(new LoginResponse(response.getStatus(), "Inicio de sesion exitoso"));
            } else if (response.getHttpError().equals("403")) {
                return ResponseEntity.status(403)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);

            } else if (response.getHttpError().equals("404")) {
                return ResponseEntity.status(404)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);

            } else if (response.getHttpError().equals("422")) {
                return ResponseEntity.status(422)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginResponse("error", "500", "Error interno del servidor"));
        }
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = authService.register(registerRequest);
            if ("success".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);

            }
            else if(response.getHttpError().equals("409")){
                return ResponseEntity.status(409)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }
            else if(response.getHttpError().equals("401")){
                return ResponseEntity.status(401)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);

            }
            else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body(response);
            }
        } catch (Exception e) {

            return ResponseEntity.status(500)
                    .body(new RegisterResponse("error", "Error interno del servidor"));

        }
    }

    @PostMapping(value = "/logout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> logout(@Valid @RequestBody String token) {
        try {

            String response = authService.logout(token);


            if (response.equals("success")) {
                return ResponseEntity.ok()
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body("Logout success");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body("Logout fallido: Token inválido o no encontrado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                    .body("Error interno del servidor");

        }
    }


}
