package com.ufpb.br.apps4society.my_trace_table_manager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ufpb.br.apps4society.my_trace_table_manager.entity.User;
import com.ufpb.br.apps4society.my_trace_table_manager.service.exception.TokenException;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;

@Service
public class TokenProvider {
    @Value("${app.secret}")
    private String secret;
    private Algorithm algorithm;
    private final long EXPIRATION_TIME_IN_SECONDS = 86400;

    @PostConstruct
    public void setUp(){
        Base64.getEncoder().encode(secret.getBytes());
        algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public String generateToken(User user){
        try {
            return JWT.create()
                    .withSubject(String.valueOf(user.getId()))
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(expirationToken())
                    .sign(algorithm)
                    .strip();
        } catch (JWTCreationException e){
            throw new TokenException(e.getMessage());
        }
    }

    private Instant expirationToken() {
        return LocalDateTime.now().plusSeconds(EXPIRATION_TIME_IN_SECONDS).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubjectByToken(String token){
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            throw new TokenException(e.getMessage());
        }
    }

    public long getExpirationTimeInSeconds() {
        return EXPIRATION_TIME_IN_SECONDS;
    }
}
