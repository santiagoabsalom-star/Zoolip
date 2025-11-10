package com.surrogate.Zoolip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surrogate.Zoolip.utils.DaoAuthenticationProviderWithId;
import com.surrogate.Zoolip.utils.UserDetailsServiceWithId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JWTAuthFilter jwtAuthFilter;
    private final LoggingFilter loggingFilter;
    private final UserDetailsServiceWithId userDetailsService;
    private final CustomOAuth2LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers(
                                "/VAADIN/**",
                                "/frontend/**",
                                "/webjars/**",
                                "/images/**",
                                "/icons/**",
                                "/themes/**",
                                "/manifest.webmanifest",
                                "/sw.js",
                                "/offline-page.html"
                        ).permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/admin/register").permitAll()
                        .requestMatchers("/api/auth/hola").permitAll()
                        .requestMatchers("/web/main").permitAll()
                        .requestMatchers("/web/**").permitAll()
                        .requestMatchers("/.well-known/appspecific/com.chrome.devtools.json").permitAll()
                        .requestMatchers("/web/main/**").permitAll()
                        .requestMatchers("/actuator").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/institucion/obtenerSolicitudes").hasAnyRole("SYSTEM")
                        .requestMatchers("/api/institucion/obtenerPorId").hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .requestMatchers("api/institucion/obtenerTodas").hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .requestMatchers("/api/institucion/actualizar").hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .requestMatchers("/api/institucion/solicitudInstitucion").permitAll()
                        .requestMatchers("/api/institucion/agregar").hasAnyRole("ADMIN", "ADMINISTRADOR", "SYSTEM")
                        .requestMatchers("/api/institucion/eliminar").hasAnyRole("ADMIN", "ADMINISTRADOR", "SYSTEM")
                        .requestMatchers("/api/institucion/obtenerPorIdUsuario").hasAnyRole("ADMIN", "ADMINISTRADOR", "SYSTEM")
                        .requestMatchers("/api/mascotas/**").hasAnyRole("ADMIN", "ADMINISTRADOR","ADOPTANTE","USUARIO")
                        .requestMatchers("/api/auth/me").permitAll()
                        .requestMatchers("/api/publicacion/obtenerTodas").hasAnyRole("ADMIN", "ADMINISTRADOR","ADOPTANTE","USUARIO")
                        .requestMatchers("/api/publicacion/obtenerPublicacionesPublicas").permitAll()
                        .requestMatchers("/api/publicacion/obtenerPorId").hasAnyRole("ADMIN", "ADMINISTRADOR","ADOPTANTE","USUARIO")
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/post/**").permitAll()
                        .requestMatchers("/chat/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(endpoint -> endpoint.baseUri("/login/oauth2/code/*"))
                        .successHandler(loginSuccessHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                        .contentSecurityPolicy(csp -> csp.policyDirectives((
                                "default-src 'self'; " +
                                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "font-src 'self' data:;"
                        ))))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();


    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://192.168.0.45:5173",
                "http://localhost:5174",
                "null",
                "http://172.18.0.1:5173",
                "http://172.18.0.1:3000",
                "http://172.20.10.8:5173"

        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-CSRF-TOKEN",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Allow-Origin",
                "Access-Control-Request-Headers",
                "X-Requested-With"

        ));
        configuration.setExposedHeaders(List.of("Authorization", "Id-Usuario", "Nombre-Usuario"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProviderWithId authProvider = new DaoAuthenticationProviderWithId();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {


        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public String success() {
        return "success";
    }

    @Bean
    public String error() {
        return "error";
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
