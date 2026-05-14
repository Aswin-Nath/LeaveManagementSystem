package com.kira.leavemanagement.request_bodies.LeaveRequest;

import lombok.Data;
import java.util.List;

import com.kira.leavemanagement.entity.IndividualDate;
@Data
public class Update {
    private String reason;
    private List<IndividualDate> dates;
}
