package com.sosmoothocp.app.config;

import com.sosmoothocp.app.persistence.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

//@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Service
public class JwtUtil {

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
        byte[] secretAsByte = Decoders.BASE64.decode(JwtConstants.SECRET);
        return Keys.hmacShaKeyFor(secretAsByte);
    }

    public String extractUserEmail(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts.parser().setSigningKey(generateKey()).build().parseClaimsJws(jwt).getBody();
    }
}

