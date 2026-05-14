package com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView;

import com.kira.leavemanagement.entity.Employee;

import lombok.Data;

@Data
public class Info {
    private Integer id;
    private String name;
    private String mail;
    private String phoneNumber;
    private Integer manager_id;
    public Info(Employee emp) {
        this.id = emp.getId();
        this.name = emp.getName();
        this.mail = emp.getMail();
        this.phoneNumber = emp.getPhoneNumber();
        this.manager_id = emp.getManagerId();
    }

}
