package com.kira.leavemanagement.response_bodies.LeaveDetails.ManagerView;

import java.time.LocalDate;

import com.kira.leavemanagement.entity.IndividualDate;
import com.kira.leavemanagement.entity.Leaves;

import lombok.Data;
import java.util.List;
@Data
public class IndividualLeaveDetails {
    private String emp_name;
    private LocalDate applied_on;
    private String reason;
    private String approval_status;
    private List<IndividualDate> dates;
    private String comments;
    
    public IndividualLeaveDetails(Leaves leave){
        this.emp_name=leave.getEmployee().getName();
        this.applied_on=leave.getAppliedOn();
        this.reason=leave.getReason();
        this.approval_status=leave.getApprovalStatus();
        this.dates=leave.getDates();
        this.comments=leave.getComments();
    }
    
}
