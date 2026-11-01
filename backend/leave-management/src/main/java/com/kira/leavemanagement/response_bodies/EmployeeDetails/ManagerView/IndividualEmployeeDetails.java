package com.kira.leavemanagement.response_bodies.EmployeeDetails.ManagerView;

import lombok.Data;

@Data
public class IndividualEmployeeDetails {
    private Integer id;
    private String name;
    private String mail;
    private String phoneNumber;
            private Integer usedPaidLeaves;
    private Integer allowedPaidLeaves;
            public IndividualEmployeeDetails(Integer id, String name, String mail, String phoneNumber, Integer usedPaidLeaves,
            Integer allowedPaidLeaves) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.phoneNumber = phoneNumber;
        this.usedPaidLeaves = usedPaidLeaves;
        this.allowedPaidLeaves = allowedPaidLeaves;
    }
}
