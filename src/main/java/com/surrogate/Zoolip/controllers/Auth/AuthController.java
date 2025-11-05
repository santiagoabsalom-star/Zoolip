package com.surrogate.Zoolip.controllers.Auth;

import com.surrogate.Zoolip.models.DTO.UsuarioDto;
import com.surrogate.Zoolip.models.login.LoginRequest;
import com.surrogate.Zoolip.models.login.LoginResponse;
import com.surrogate.Zoolip.models.register.RegisterRequest;
import com.surrogate.Zoolip.models.register.RegisterResponse;
import com.surrogate.Zoolip.services.auth.AuthService;
import com.surrogate.Zoolip.services.auth.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final String error;


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            LoginResponse response = authService.login(loginRequest, request.getRemoteAddr());

            if (response.getHttpCode() == 200) {
                ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", response.getToken())
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .sameSite("Strict")
                        .maxAge(Duration.ofMinutes(90))
                        .build();
                return ResponseEntity.ok()

                        .header(HttpHeaders.SET_COOKIE, cookie.toString())

                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(new LoginResponse(response.getStatus(), "Inicio de sesion exitoso"));
            } else {
                return ResponseEntity.status(response.getHttpCode())
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginResponse(error, 500, "Error interno del servidor"));
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

            } else {
                return ResponseEntity.status(response.getHttpCode())
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }
        } catch (Exception e) {

            return ResponseEntity.status(500)
                    .body(new RegisterResponse(error, "Error interno del servidor"));

        }
    }

    @PostMapping(value = "/admin/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RegisterResponse> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse response = authService.registerAdmin(registerRequest);
            if (200 == (response.getHttpCode())) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);

            } else {
                return ResponseEntity.status(response.getHttpCode())
                        .header("X-Content-Type-Options", "nosniff")
                        .header("X-Frame-Options", "DENY")
                        .header("X-XSS-Protection", "1; mode=block")
                        .body(response);
            }
        } catch (Exception e) {

            return ResponseEntity.status(500)
                    .body(new RegisterResponse(error, "Error interno del servidor" + e.getMessage()));

        }
    }


    @PostMapping(value = "/logout", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            String token = Objects.requireNonNull(Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("AUTH_TOKEN")).findFirst().orElse(null)).getValue();

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

    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<Optional<UsuarioDto>> me(HttpServletRequest request) {
        if (request.getCookies() == null || request.getCookies().length == 0) {
            return ResponseEntity.ofNullable(null);

        }
        String token = Objects.requireNonNull(Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("AUTH_TOKEN")).findFirst().orElse(null)).getValue();
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<UsuarioDto> usuarioDto = usuarioService.me(token);
        if (usuarioDto.isEmpty()) {

            return new ResponseEntity<>(HttpStatus.NO_CONTENT) ;

        }
        if(usuarioDto.equals(Optional.of(403).map(e->null))){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(usuarioDto, HttpStatus.OK);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<UsuarioDto>> getAccounts(HttpServletRequest request) {

        if (request.getCookies() == null || request.getCookies().length == 0) {
            return ResponseEntity.ofNullable(null);

        }
        String token = Objects.requireNonNull(Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("AUTH_TOKEN")).findFirst().orElse(null)).getValue();
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<UsuarioDto> usuarioDto = usuarioService.findAllByEmail(token);
        if (usuarioDto == null) {

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }
        return new ResponseEntity<>(usuarioDto, HttpStatus.OK);
    }

}

