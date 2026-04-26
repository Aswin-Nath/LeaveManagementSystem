package com.kira.leavemanagement.request_bodies;

import lombok.Data;

public class Creates {
    @Data
    public static class CreateEmployee{
        private String name;
        private String mail;
        private String password;
        private String phone_number;
        private Integer manager_id;
    }

}
