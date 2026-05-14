package com.kira.leavemanagement.response_bodies.LeaveDetails.EmployeeView;

import java.time.LocalDate;

import com.kira.leavemanagement.entity.IndividualDate;
import com.kira.leavemanagement.entity.Leaves;

import lombok.Data;
import java.util.List;

@Data
public class IndividualLeaveDetails {
    private LocalDate applied_on;
    private String reason;
    private String approval_status;
    private List<IndividualDate> dates;
    private Integer approver_id;
    private String comments;
    public IndividualLeaveDetails(Leaves leave){
        this.applied_on=leave.getAppliedOn();
        this.reason=leave.getReason();
        this.approval_status=leave.getApprovalStatus();
        this.dates=leave.getDates();
        if(leave.getApprover()!=null){
            this.approver_id=leave.getApprover().getId();        
        }
        this.comments=leave.getComments();
    }
}
