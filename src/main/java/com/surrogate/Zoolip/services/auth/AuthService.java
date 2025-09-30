package com.surrogate.Zoolip.services.auth;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.login.LoginRequest;
import com.surrogate.Zoolip.models.login.LoginResponse;
import com.surrogate.Zoolip.models.register.RegisterResponse;
import com.surrogate.Zoolip.models.register.RegisterRequest;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import com.surrogate.Zoolip.utils.UserDetailsWithId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class AuthService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";
    private final BCryptPasswordEncoder encryptor = new BCryptPasswordEncoder(8);
    private final JWTService jwtService;


    private final AuthenticationManager authManager;


    private final UsuarioRepository usuarioRepository;

    public AuthService(JWTService jwtService, AuthenticationManager authManager, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        try {
            if(loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ) {
                return new LoginResponse("error", "422", "El nombre de usuario es requerido");
            }
            if(loginRequest.getUsername().length() < MIN_PASSWORD_LENGTH && loginRequest.getUsername().length() > MAX_USERNAME_LENGTH) {
                return new LoginResponse("error","422","El nombre de usuario debe tener entre 3 y 20 caracteres alfanuméricos");
            }
            if(loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty() ) {
                return new LoginResponse("error", "422", "La constrasenia es requerida");
            }

            if (!isValidLoginRequest(loginRequest)) {
                return new LoginResponse("error", "422", "Parámetros inválidos");
            }
            if(!usuarioRepository.existsByNombre(loginRequest.getUsername())){
                return new LoginResponse("error", "404", "Usuario no encontrado");
            }


            long startTimeauth = System.currentTimeMillis();
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                UserDetailsWithId userDetails = (UserDetailsWithId) authentication.getPrincipal();

                assert userDetails != null;
                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");
                long endTimeauth = System.currentTimeMillis();
                log.info("Tiempo de espera de autenticacion: " + (endTimeauth - startTimeauth) + "ms");

                String token = jwtService.generateToken(userDetails.getUsername(), role);


                return new LoginResponse("success", token, userDetails.getUsername(), userDetails.getId());
            }

            return new LoginResponse("error", "401", "Autenticación fallida");
        } catch (BadCredentialsException e) {
            return new LoginResponse("error", "401", "Credenciales inválidas");
        } catch (Exception e) {
            return new LoginResponse("error", "500", "Error en el servidor: " + e.getMessage());
        }
    }

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        try {
            if (!isValidRegisterRequest(registerRequest)) {
                return new RegisterResponse("La contrasenia tiene que tener mas de 8 caracteres", "401");
            }


            if (usuarioRepository.existsByNombre(registerRequest.getUsername())) {
                return new RegisterResponse("error","409","El nombre ya existe");
            }

            Usuario newUsuario = new Usuario();
            newUsuario.setNombre(registerRequest.getUsername());
            newUsuario.setPasswordHash(encryptor.encode(registerRequest.getPassword()));
            newUsuario.setRol(registerRequest.getRol() != null ? registerRequest.getRol() : "USER");

            usuarioRepository.save(newUsuario);
            return new RegisterResponse("success", "Registro exitoso");
        } catch (Exception e) {
            return new RegisterResponse("error", "Error en el registro: " + e.getMessage());
        }
    }

    public String logout(String token) {
        log.info("Este es el token del usuario: {}", token);

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token no puede ser nulo o vacío");
        }
        String m = jwtService.InvalidateToken(token);
        if (m.equals("success")) {
            return "success";
        }

        return "error";
    }

    private boolean isValidLoginRequest(LoginRequest request) {
        return request != null &&
                StringUtils.hasText(request.getUsername()) &&
                StringUtils.hasText(request.getPassword());
    }

    private boolean isValidRegisterRequest(RegisterRequest request) {
        return request != null &&
                StringUtils.hasText(request.getUsername()) &&
                request.getUsername().matches(USERNAME_PATTERN) &&
                StringUtils.hasText(request.getPassword()) &&
                request.getPassword().length() >= MIN_PASSWORD_LENGTH;
    }


}
