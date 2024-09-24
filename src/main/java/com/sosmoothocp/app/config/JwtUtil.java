package com.sosmoothocp.app.config;

import com.sosmoothocp.app.persistence.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Service
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(User user, Map<String, Object> extraClaims) {

        Date issuedAt = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + JwtConstants.EXPIRATION_TIME))
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key generateKey() {
        byte[] secretAsByte = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(secretAsByte);
    }

    public String extractUserEmail(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser().setSigningKey(generateKey()).build().parseClaimsJws(jwt).getBody();
    }

    public String generateRefreshToken(User user) {
        Date issuedAt = new Date(System.currentTimeMillis());

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(issuedAt)
                .expiration(new Date(issuedAt.getTime() + JwtConstants.REFRESH_EXPIRATION_TIME))
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser() // Usa parserBuilder() con la versione aggiornata
                    .setSigningKey(getSignInKey()) // Imposta la chiave di firma
                    .build() // Costruisce il parser
                    .parseClaimsJws(token); // Analizza il token true;
            return true;
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired.");
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token.");
        }
    }

    private Key getSignInKey() {
        return generateKey();
    }
}

