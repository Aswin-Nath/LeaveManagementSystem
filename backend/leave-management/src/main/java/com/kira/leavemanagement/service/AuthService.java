package com.kira.leavemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.repository.EmployeeRepository;
import com.kira.leavemanagement.utils.AuthUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.HashMap;
@Service
public class AuthService {
    @Autowired
    private BCryptPasswordEncoder encoder;
    private final EmployeeRepository repo;
    private final AuthUtil util;
    public AuthService(EmployeeRepository repo,AuthUtil util){
        this.repo=repo;
        this.util=util;
    }

    public ResponseEntity<?> Login(String mail,String password,HttpServletResponse response){
        Employee emp = repo.findByMail(mail);
        if(emp==null){
            throw new RuntimeException("Employee not found");
        }
        if(!encoder.matches(password,emp.getHashed_password())){
            throw new RuntimeException("Password invalid");
        }
        emp.setSession_version(emp.getSession_version()+1);

        String accessToken = util.getAccessToken(emp);
        String refreshToken = util.getRefreshToken(emp);
        repo.save(emp);

        Map<String,String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken",refreshToken);
        Cookie cookie = new Cookie("refreshToken",result.get("refreshToken"));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(util.getRefreshLife().intValue());
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("accessToken",result.get("accessToken")));
    }

    public ResponseEntity<?> Logout(HttpServletResponse response,HttpServletRequest request){
        String refreshToken = util.extractToken(request);
        if(refreshToken==null){
            throw new RuntimeException("Token is not present");
        }

        Claims claims;
        System.out.println("REFRESH TOKEN: " + refreshToken);
        try{
            claims=util.validate(refreshToken);
        }
        catch(Exception err){
            throw new RuntimeException("Token Invalid"+err.getMessage());
        }
        Integer empId = Integer.parseInt(claims.getSubject());
        Employee emp = repo.findById(empId).orElseThrow(()-> new RuntimeException("Employee not found"));
        emp.setSession_version(emp.getSession_version()+1);
        Cookie cookie = new Cookie("refreshToken","");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        repo.save(emp);
        return ResponseEntity.ok("Logged out");
    }

    public ResponseEntity<?> Refresh(HttpServletResponse response,HttpServletRequest request){
        String refreshToken = util.extractToken(request);
        if(refreshToken==null){
            throw new RuntimeException("No Cookies");
        }
        System.out.println("REFRESH TOKEN: " + refreshToken);
        Claims claims;
        try{
            claims = util.validate(refreshToken);
        }
        catch(Exception err){
            throw new RuntimeException("Invalid token");
        }

        Integer empId = Integer.parseInt(claims.getSubject());
        Integer session_version = claims.get("session_version",Integer.class);
        Employee emp = repo.findById(empId).orElseThrow(()-> new RuntimeException("Employee not found"));
        if(!emp.getSession_version().equals(session_version)){
            throw new RuntimeException("Invalid token");
        }
        String newAccessToken = util.getAccessToken(emp);

        return ResponseEntity.ok(Map.of("accessToken",newAccessToken));
    }
}
