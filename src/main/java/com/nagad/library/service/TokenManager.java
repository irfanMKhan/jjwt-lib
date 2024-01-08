package com.nagad.library.service;


import com.nagad.library.model.TokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


public class TokenManager {

    private final String CLAIM_USER;
    private final Long EXPIRATION_HOUR;
    private final String TOKEN_SECRET_KEY;

    /**
     * default expiration is after 11 hour;
     */
    public TokenManager() {
        this.CLAIM_USER = "usr";
        this.TOKEN_SECRET_KEY = "itsNiceToBeADeveloper-MoreYearToCome-2024-NowAdd-A-LongestWord-%pneumonoultramicroscopicsilicovolcanoconiosis%";
        this.EXPIRATION_HOUR = 11L;
    }

    public TokenManager(String SECRET_KEY, Long EXPIRATION_HOUR) {
        this.CLAIM_USER = "usr";
        this.TOKEN_SECRET_KEY = SECRET_KEY;
        this.EXPIRATION_HOUR = EXPIRATION_HOUR;
    }

    private String encryptKey() {
        return Encoders.BASE64.encode(TOKEN_SECRET_KEY.getBytes());
    }

    private String decryptKey(String encryptedValue) {
        return Arrays.toString(Decoders.BASE64.decode(encryptedValue));
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(encryptKey().getBytes());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    }

    private String buildToken(Map<String, Object> claims, String authentication, Long expiration) {
        Date tokenIssuedDate = new Date(System.currentTimeMillis());
        Date tokenExpireDate = new Date(tokenIssuedDate.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(tokenIssuedDate)
                .expiration(tokenExpireDate)
                .subject(authentication)
                .signWith(getSignInKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateToken(String authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER, authentication);
        return generateToken(claims, authentication);
    }

    public String generateToken(Map<String, Object> claims, String authentication) {
        return buildToken(claims, authentication, TimeUnit.HOURS.toMillis(EXPIRATION_HOUR));
    }

    public String generateRefreshToken(String authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USER, authentication);
        return generateRefreshToken(claims, authentication);
    }

    public String generateRefreshToken(Map<String, Object> claims, String authentication) {
        return buildToken(claims, authentication, TimeUnit.HOURS.toMillis(EXPIRATION_HOUR));
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    public TokenDTO validateToken(String token) {
        TokenDTO validateToken = new TokenDTO();

        try {
            Claims claims = getClaims(token);

            validateToken.setSubject(claims.get(CLAIM_USER).toString());
            validateToken.setMessage("Token Validated");
            validateToken.setStatus(true);

        } catch (MalformedJwtException exception) {
            validateToken.setMessage("Invalid token :: " + exception.getMessage());
        } catch (ExpiredJwtException exception) {
            validateToken.setMessage("Token is expired :: " + exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            validateToken.setMessage("Token is unsupported :: " + exception.getMessage());
        } catch (IllegalArgumentException exception) {
            validateToken.setMessage("Token claims is empty: :: " + exception.getMessage());
        } catch (JwtException exception) {
            validateToken.setMessage("JWT exception is thrown: :: " + exception.getMessage());
        }

        return validateToken;
    }

}
