package com.taskmanager.task_manager_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
public class ServicoJwt {

    @Value("${app.jwt.secret}")
    private String chaveSecreta;

    @Value("${app.jwt.expiration}")
    private long expiracaoToken;

    @Value("${app.jwt.refresh-expiration}")
    private long expiracaoRefreshToken;

    public String gerarAccessToken(UserDetails userDetails) {
        return construirToken(new HashMap<>(), userDetails, expiracaoToken);
    }

    public String gerarRefreshToken(UserDetails userDetails){
        return construirToken(new HashMap<>(), userDetails, expiracaoRefreshToken);
    }

    private String construirToken(Map<String, Object> claimsExtras,
                                  UserDetails userDetails,
                                  long expiracao) {
        return Jwts.builder()
                .claims(claimsExtras)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiracao))
                .signWith(getChaveAssinatura(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean isTokenValido(String token, UserDetails userDetails) {
        final String email = extrairEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }

    private boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    private Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    public <T> T extrairClaim(String token, Function<Claims, T> resolvedorClaim) {
        final Claims claims = extrairTodosClaims(token);
        return resolvedorClaim.apply(claims);
    }

    private Claims extrairTodosClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getChaveAssinatura() {
        byte[] bytesChave = chaveSecreta.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytesChave);
    }

}
