package com.surrogate.Zoolip.services.auth;

import com.surrogate.Zoolip.events.UsuarioCreado;
import com.surrogate.Zoolip.events.UsuarioNotifier;
import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.models.login.LoginRequest;
import com.surrogate.Zoolip.models.login.LoginResponse;
import com.surrogate.Zoolip.models.register.RegisterRequest;
import com.surrogate.Zoolip.models.register.RegisterResponse;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import com.surrogate.Zoolip.utils.UserDetailsWithId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class AuthService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioNotifier usuarioNotifier;


    public AuthService(JWTService jwtService , PasswordEncoder passwordEncoder, AuthenticationManager authManager, UsuarioRepository usuarioRepository, UsuarioNotifier usuarioNotifier) {
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.usuarioNotifier = usuarioNotifier;

        this.passwordEncoder= passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        try {
            if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()) {
                return new LoginResponse("error", 422, "El nombre de usuario es requerido");
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return new LoginResponse("error", 422, "La constrasenia es requerida");
            }

            if (!isValidLoginRequest(loginRequest)) {
                return new LoginResponse("error", 422, "Parámetros inválidos");
            }
            if (!usuarioRepository.existsByNombre(loginRequest.getUsername())) {
                return new LoginResponse("error", 404, "Usuario no encontrado");
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

                String token = jwtService.generateTokenWithId(userDetails.getUsername(), role, userDetails.getId());


                return new LoginResponse("success", token, userDetails.getUsername(), (long) userDetails.getId());
            }

            return new LoginResponse("error", 401, "Autenticación fallida");
        } catch (BadCredentialsException e) {
            return new LoginResponse("error", 403, "Contrasenia incorrecta");
        } catch (Exception e) {
            return new LoginResponse("error", 500, "Error en el servidor: " + e.getMessage());
        }
    }

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        try {
            if (!isValidRegisterRequest(registerRequest)) {
                return new RegisterResponse("error", 401, "La constrasenia tiene que tener 8 digitos o mas");
            }
            if (registerRequest.getRol() == null || registerRequest.getRol().isEmpty()) {
                return new RegisterResponse("error", 401, "El rol es requerido");
            }
            if (registerRequest.getRol().equals("ADMINISTRADOR")) {
                return new RegisterResponse("error", 403, "El rol no puede ser administrador");
            }
            if (usuarioRepository.existsByNombre(registerRequest.getUsername())) {

                return new RegisterResponse("error", 409, "El nombre ya existe");

            }


            Usuario newUsuario = new Usuario();
            newUsuario.setNombre(registerRequest.getUsername());
            newUsuario.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

            newUsuario.setRol(registerRequest.getRol() != null ? registerRequest.getRol() : "USER");

            usuarioRepository.save(newUsuario);

            return new RegisterResponse("success", "Registro exitoso");
        } catch (Exception e) {
            return new RegisterResponse("error", "Error en el registro: " + e.getMessage());
        }
    }

    @Transactional
    public RegisterResponse registerAdmin(RegisterRequest registerRequest) {
        try {
            if (!isValidRegisterRequest(registerRequest)) {
                return new RegisterResponse("error", 401, "La constrasenia tiene que tener 8 digitos o mas");
            }
            if (registerRequest.getRol() == null || registerRequest.getRol().isEmpty()) {
                return new RegisterResponse("error", 401, "El rol es requerido");
            }
            if (!registerRequest.getRol().equals("ADMINISTRADOR")) {
                return new RegisterResponse("error", 403, "El rol tiene que ser administrador");
            }

            if (usuarioRepository.existsByNombre(registerRequest.getUsername())) {

                return new RegisterResponse("error", 409, "El nombre ya existe");

            }


            Usuario newUsuario = new Usuario();
            newUsuario.setNombre(registerRequest.getUsername());
            newUsuario.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

            newUsuario.setRol(registerRequest.getRol() != null ? registerRequest.getRol() : "USER");

            usuarioRepository.save(newUsuario);
            usuarioNotifier.publish(new UsuarioCreado(newUsuario));
            return new RegisterResponse("success",200, "Registro exitoso");
        } catch (Exception e) {
            return new RegisterResponse("error",500, "Error en el registro: " + e.getMessage());
        }
    }

    public String logout(String token) {
        log.info("Este es el token del usuario: {}", token);

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token no puede ser nulo o vacío");
        }
        String response = jwtService.InvalidateToken(token);
        if (response.equals("success")) {
            return "success";
        }

        return "error";
    }

    public Usuario me(Long id) {
        return usuarioRepository.findById(id).orElse(null);
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
