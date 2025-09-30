package com.surrogate.Zoolip.config;

import com.surrogate.Zoolip.services.auth.JWT.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
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


    public CustomOAuth2LoginSuccessHandler(JWTService jwtService)  {
        this.jwtService = jwtService;

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
            log.info("Token generado: {}" , jwt);


            String frontendRedirectUrl = "http://localhost:3050/api/auth/login";
            String redirectUrl = frontendRedirectUrl + "#token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);
            response.sendRedirect(redirectUrl);

        }
    }
}