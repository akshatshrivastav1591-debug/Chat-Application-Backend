package com.Project1.ChatApplication.Security.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtFilterServiceClass {
    @Value("${jwt.secret}")
    private String secretKey;


    public  String getJwtTokens(String username,String externalUserID){
        Map<String,Object> claims=new HashMap<>();
        claims.put("userID",externalUserID);

        return Jwts.builder()
                .claims()
                .empty()
                .add(claims)
                .and()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(getKey())
                .compact();


    }
    private SecretKey getKey(){
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private  <T>T extractClaims(String token, Function<Claims,T>claimResolver){
        final  Claims claims=extractAllClaims(token);
        return  claimResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {
        try {


            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (Exception e){

            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
    public String extractUserName(String token) {

        return extractClaims(token,Claims::getSubject);
    }


    public boolean validateToken(String token, UserDetails userdetails) {
        final  String userName=extractUserName(token);

        return (userName.equals(userdetails.getUsername()) && !isTokenExpired(token));
    }
  public String extractUserId(String token){
        return extractClaims(token,claims -> claims.get("userID",String.class));
    }


    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return  extractClaims(token,Claims::getExpiration);
    }
}
