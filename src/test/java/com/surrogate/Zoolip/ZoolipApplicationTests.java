package com.surrogate.Zoolip;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Slf4j
@SpringBootTest
class ZoolipApplicationTests {

    static int i= 0;
    static Random random= new Random();
    static Map<String, Object> claims = new HashMap<>();
    static HashSet<String> tokens = new HashSet<>();
    static SecretKey key = Keys.hmacShaKeyFor("secret19910920990219102ncjnncjalmkcsalklkasc".getBytes());
    static String[] names= "RAPIST,NIGGER,DOWNI,MONGOLOID,STRING,PORNO,SEXO,PORNA,SUNX,PORIOQL,NMSIAM,KALAKL,KAAKAAKAAKAA,NEGRODEMIERDAAA,PORCULOTELADAN".split(",");
    static String[] roles= "ADMINISTRADOR,USUARIO,NIGGER".split(",");

    public static void main(String[] args) {
            while(i<=10) {
                i++;
                long current = System.currentTimeMillis();
                String token = generateTokenWithId(names[random.nextInt(names.length)],roles[random.nextInt(roles.length)], i);
                long dur = System.currentTimeMillis() - current;
                log.info("nombre extraido del token, {}", extractUsername(token));
                log.info("Duracion de generacion de token {} ms", dur);
                log.info("Id_usuario: {}", extractId(token));
            }
    }

    public static String generateTokenWithId(String username, String role, Integer id_usuario) {
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

    static String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 19929182189892L))
                .and()
                .signWith(key)
                .compact();

    }

    public static Integer extractId(String token) {
        return (Integer) extractClaim(token, Claims -> Claims.get("id_usuario"));
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

}
