package com.kira.leavemanagement.utils;

import org.springframework.stereotype.Component;

import com.kira.leavemanagement.entity.Employee;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
@Component
public class AuthUtil {
    
    @Value("${JWT_SECRET}")
    private String secret;

    @Value("${ACCESS_TOKEN_LIFE}")
    private Long accessLife;

    @Value("${REFRESH_TOKEN_LIFE}")
    private Long refreshLife;

    public Long getAccessLife(){
        return accessLife;
    }
    public Long getRefreshLife(){
        return refreshLife;
    }
    private Key getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String getAccessToken(Employee emp){
        return Jwts.builder()
        .setSubject(emp.getId().toString())
        .setExpiration(new Date(System.currentTimeMillis()+accessLife*1000L))
        .setIssuedAt(new Date())
        .claim("session_version",emp.getSession_version())
        .signWith(getKey(),SignatureAlgorithm.HS256)
        .compact();
    }
    public String getRefreshToken(Employee emp){
        return Jwts.builder()
        .setSubject(emp.getId().toString())
        .setExpiration(new Date(System.currentTimeMillis()+refreshLife*1000L))
        .setIssuedAt(new Date())
        .claim("session_version",emp.getSession_version())
        .signWith(getKey(),SignatureAlgorithm.HS256)
        .compact();
    }

    public Claims validate(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

    public String extractToken(HttpServletRequest request){
        if(request.getCookies()==null){
            return null;
        }
        for(Cookie cookie : request.getCookies()){
            if("refreshToken".equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
}
