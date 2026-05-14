package com.kira.leavemanagement.request_bodies.EmployeeRequest;

import lombok.Data;

@Data
public class CreateEmployee {
    private String name;
    private String mail;
    private String password;
    private String phone_number;
    private Integer manager_id;
    private Integer allowed_paid_days;
}
