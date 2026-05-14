package com.kira.leavemanagement.response_bodies.EmployeeDetails.ManagerView;

import lombok.Data;

@Data
public class IndividualEmployeeDetails {
    private Integer id;
    private String name;
    private String mail;
    private String phoneNumber;
    private Integer remPaidLeaves;
    private Integer allowedPaidLeaves;
    public IndividualEmployeeDetails(Integer id, String name, String mail, String phoneNumber, Integer remPaidLeaves,
            Integer allowedPaidLeaves) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.phoneNumber = phoneNumber;
        this.remPaidLeaves = remPaidLeaves;
        this.allowedPaidLeaves = allowedPaidLeaves;
    }
}
