package com.kira.leavemanagement.response_bodies;

import com.kira.leavemanagement.entity.Employee;

import lombok.Data;
@Data
public class EmployeeResponse {
    private Integer id;
    private String name;
    private String mail;
    private String phoneNumber;
    private Integer manager_id;

    public EmployeeResponse(Employee emp){
        this.id=emp.getEmployee_id();
        this.name=emp.getName();
        this.mail=emp.getMail();
        this.phoneNumber=emp.getPhoneNumber();
        this.manager_id=emp.getManager_id();
    }
}
