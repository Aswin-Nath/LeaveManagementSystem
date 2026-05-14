package com.kira.leavemanagement.request_bodies.AuthRequest;

import lombok.Data;

@Data
public class Login {
    private String mail;
    private String password;
}
