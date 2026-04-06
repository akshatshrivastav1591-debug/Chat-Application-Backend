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

//    public  JwtFilterServiceClass(){
//        secretKey=generateSecretKey();
//    }
//
//    private String generateSecretKey() {
//        try {
//            KeyGenerator keygen=KeyGenerator.getInstance("HmacSHA256");
//            SecretKey secretKey1=keygen.generateKey();
//            System.out.println("Secret key:"+Base64.getEncoder().encodeToString(secretKey1.getEncoded()));
//            return Base64.getEncoder().encodeToString(secretKey1.getEncoded());
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("error generating key:",e);
//
//        }
//    }
    public  String getJwtTokens(String username,String externalUserID,boolean isFirstLogin){
        Map<String,Object> claims=new HashMap<>();
        claims.put("userID",externalUserID);
        claims.put("isFirstLogin",isFirstLogin);
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
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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

    public  boolean extractIsFirstLogin(String token){
        return  extractClaims(token,claims -> claims.get("isFirstLogin",Boolean.class));
    }
    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return  extractClaims(token,Claims::getExpiration);
    }
}
