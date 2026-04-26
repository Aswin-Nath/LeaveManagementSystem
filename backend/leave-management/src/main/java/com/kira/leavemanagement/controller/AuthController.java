package com.kira.leavemanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kira.leavemanagement.request_bodies.Auth;
import com.kira.leavemanagement.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService service;

    public AuthController(AuthService service){
        this.service=service;
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody Auth.Login req,HttpServletResponse response){
        return service.Login(req.getMail(), req.getPassword(),response);

    }
    @PostMapping("/logout")
    public ResponseEntity<?> Logout(HttpServletResponse response,HttpServletRequest request){
        return service.Logout(response,request);
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> Refresh(HttpServletResponse response,HttpServletRequest request){
        return service.Refresh(response, request);
    }
}
