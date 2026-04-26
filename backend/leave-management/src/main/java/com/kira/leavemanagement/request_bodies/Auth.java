package com.kira.leavemanagement.request_bodies;

import lombok.Data;

public class Auth {

    @Data
    public static class Login{
        private String mail;
        private String password;
    }

    @Data
    public static class Logout{
        private String mail;
    }
}
