package com.kira.leavemanagement.request_bodies.ProcessLeave;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LeaveFilterReq {
    private String status;
    private LocalDate fromDate;
    private LocalDate endDate;
    private LocalDate appliedFrom;
    private LocalDate appliedTo;
    private Integer empId;
}
