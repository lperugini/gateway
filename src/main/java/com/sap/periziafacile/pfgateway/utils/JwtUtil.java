package com.sap.periziafacile.pfgateway.utils;

import java.util.Date;
import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    private final String secret = "ROWS0BIAS0COIL0WENT0IONS0COED0OWNS0SEND0MARS0LYRA0BAWL0DAY0DEER0IS0BID0OVER0SCAT0WISE0DEAD0HO0LOY0STIR0DADE0ALAN";

    private final long expiration = 3600000L;

    public String generateToken(String username, Long id, String role) {
        return Jwts
                .builder()
                .subject(username)
                .claim("id", id)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (String) claims.get("role");
    }

    public String getIdFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (String) claims.get("id");
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

}
