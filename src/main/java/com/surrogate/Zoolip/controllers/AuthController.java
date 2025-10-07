package com.surrogate.Zoolip.controllers;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.login.LoginRequest;
import com.surrogate.Zoolip.models.login.LoginResponse;
import com.surrogate.Zoolip.models.peticiones.Response;
import com.surrogate.Zoolip.models.register.RegisterRequest;
import com.surrogate.Zoolip.models.register.RegisterResponse;
import com.surrogate.Zoolip.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

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

            if (response.getStatus().equals("success")) {
                ResponseCookie cookie= ResponseCookie.from("AUTH_TOKEN", response.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(Duration.ofMinutes(90))
                    .build();
                return ResponseEntity.ok()

                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .header("Id-Usuario", String.valueOf(response.getId()))
                        .header("Nombre-Usuario", response.getUsername())

                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(new LoginResponse(response.getStatus(), "Inicio de sesion exitoso"));
            } else {
                return ResponseEntity.status(response.getHttpError())
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginResponse("error", 500, "Error interno del servidor"));
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
            else {
                return ResponseEntity.status(response.getHttpError())
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
@PostMapping(value = "/me", consumes ="application/json",produces = "application/json")

    public ResponseEntity<Usuario> me(@RequestBody Long iduser) {
    Usuario usuario = authService.me(iduser);
    if (usuario == null) {
        return ResponseEntity.notFound().build();

    }
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(usuario);
}
}
