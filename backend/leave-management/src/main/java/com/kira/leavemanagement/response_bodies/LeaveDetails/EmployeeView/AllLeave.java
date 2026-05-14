package com.kira.leavemanagement.response_bodies.LeaveDetails.EmployeeView;

import java.time.LocalDate;

import com.kira.leavemanagement.entity.IndividualDate;
import com.kira.leavemanagement.entity.Leaves;

import lombok.Data;
import java.util.List;
@Data
public class AllLeave {
    private LocalDate applied_on;
    private String reason;
    private String approval_status;
    private List<IndividualDate> dates;

    public AllLeave(Leaves leave){
        this.applied_on=leave.getAppliedOn();
        this.reason=leave.getReason();
        this.approval_status=leave.getApprovalStatus();
        this.dates = leave.getDates();
    }
}
