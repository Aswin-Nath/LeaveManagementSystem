package com.kira.leavemanagement.request_bodies.LeaveRequest;

import com.kira.leavemanagement.entity.IndividualDate;

import lombok.Data;
import java.util.List;
@Data
public class Create {
    private String reason;
    private List<IndividualDate> dates;
    
}
