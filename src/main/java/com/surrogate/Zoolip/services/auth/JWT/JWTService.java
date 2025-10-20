package com.surrogate.Zoolip.services.auth.JWT;

import com.surrogate.Zoolip.utils.UserDetailsWithId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@NoArgsConstructor
public class JWTService {
    private final Map<String, Object> claims = new HashMap<>();
    private final HashSet<String> tokens = new HashSet<>();
    //private final HashSet<Integer> authtokens = new HashSet<>();
    //private final Random random = new Random();
    //Esto es una mala practica ya que hardcodea la jwt secret key
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    private SecretKey key;


    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalStateException("JWT secret key must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    /** Para implementar despues.
      @Scheduled(fixedRate = 1000*60*60)
    private void Token() {
        authtokens.clear();
        Integer integer= random.nextInt();
        authtokens.add(integer);
        log.info("Token cambiado a {}", integer);
        authtokens.iterator().forEachRemaining(authtoken -> log.info("Token {}", authtoken));
    }
**/
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Integer extractId(String token) {
        return (Integer) extractClaim(token, Claims -> Claims.get("id_usuario"));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }


    public String generateToken(String username, String role) {

        claims.put("role", role);
        log.info(claims.toString());
        String token = createToken(claims, username);
        log.info("Claim=id_usuario: {}", extractId(token));
        claims.clear();
        try {
            tokens.add(token);
        } catch (Exception e) {
            log.error("Error al guardar el token {} ", e.getMessage());
        }
        return token;
    }

    /**
     * With id_usuario para el caprichoso de horacio :D
     *
     * @param username
     * @param role
     * @param id_usuario
     * @return String token
     */
    public String generateTokenWithId(String username, String role, Integer id_usuario) {
        claims.put("role", role);
        claims.put("id_usuario", id_usuario);
        log.info("Claims: {}", claims);
        String token = createToken(claims, username);
        claims.clear();
        log.info(token);

        try {
            tokens.add(token);
        } catch (Exception e) {
            log.error("Error al guardar el token: {} ", e.getMessage());
        }
        return token;
    }


    public boolean validateToken(String token, UserDetailsWithId userDetails) {
        if (tokens.contains(token)) {
            log.info("Token encontrado");
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }
            if (extractExpiration(token).before(new Date())) {
                return false;
            }
        }

        try {
            final String username = extractUsername(token);
            log.info("Username extraído del token: {} ", username);
            return username.equals(userDetails.getUsername()) && isValidTokenFormat(token);

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidTokenFormat(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    protected String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .and()
                .signWith(key)
                .compact();

    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public String InvalidateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (token.startsWith("\"") && token.endsWith("\"")) {
            log.info("Token con comillas detectado, eliminando comillas...");
            token = token.trim().replace("\"", "");
        }
        if (!isValidTokenFormat(token)) {
            log.info("Token no válido o no almacenado: {}", token);
            return "error";
        }

        log.info("Invalidando token: [  {}  ]", token);
        tokens.remove(token);
        return "success";
    }

    public boolean isTokenStored(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (token.startsWith("\"") && token.endsWith("\"")) {
            log.info("Token con comillas detectado, eliminando comillas");
            token = token.trim().replace("\"", "");
        }
        return tokens.contains(token);
    }


}


