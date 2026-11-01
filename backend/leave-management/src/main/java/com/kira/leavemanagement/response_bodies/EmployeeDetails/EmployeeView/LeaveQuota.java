package com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView;

import lombok.Data;

@Data
public class LeaveQuota {
    private Integer usedPaidLeaves;
    private Integer allowedPaidLeaves;
    public LeaveQuota(Integer usedPaidLeaves, Integer allowedPaidLeaves) {
        this.usedPaidLeaves = usedPaidLeaves;
        this.allowedPaidLeaves = allowedPaidLeaves;
    }

}
