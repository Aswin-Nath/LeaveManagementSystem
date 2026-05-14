package com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView;

import lombok.Data;

@Data
public class LeaveQuota {
    private Integer remPaidLeaves;
    private Integer allowedPaidLeaves;
    public LeaveQuota(Integer remPaidLeaves, Integer allowedPaidLeaves) {
        this.remPaidLeaves = remPaidLeaves;
        this.allowedPaidLeaves = allowedPaidLeaves;
    }

}
