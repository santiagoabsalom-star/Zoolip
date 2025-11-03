package com.surrogate.Zoolip.config;

import com.surrogate.Zoolip.models.bussiness.Usuario;
import com.surrogate.Zoolip.repository.bussiness.UsuarioRepository;
import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@Slf4j
@Component
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public CustomOAuth2LoginSuccessHandler(JWTService jwtService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            log.info("user attributes: {}", attributes);

            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String id = (String) attributes.get("sub");
            log.info("Usuario {} autorized whigga nigga", name);
            log.info("Email : {}", email);
            log.info("id: {}", id);


            String jwt = jwtService.generateToken(name, "USUARIO");
            log.info("Token generado: {}", jwt);
            if(!usuarioRepository.existsByEmail(email)){
                registerIfNewUser(name, email);
            }

            String frontendRedirectUrl = "http://localhost:3050/api/auth/login";
            String redirectUrl = frontendRedirectUrl + "#token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);

        } else {
            log.error("Authentication is not of type OAuth2AuthenticationToken");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid authentication type");
        }

    }

    public void registerIfNewUser(String nombre, String email){
            if(!usuarioRepository.existsByNombre(nombre)){
                log.info("Registrando nuevo usuario OAuth2: {}", nombre);
                Usuario newUsuario = new Usuario();
                newUsuario.setNombre(nombre);
                newUsuario.setEmail(email);

                String randomPassword = java.util.UUID.randomUUID().toString();
                newUsuario.setPasswordHash(passwordEncoder.encode(randomPassword));
                newUsuario.setRol("USUARIOOAUTH");}

        }

}
