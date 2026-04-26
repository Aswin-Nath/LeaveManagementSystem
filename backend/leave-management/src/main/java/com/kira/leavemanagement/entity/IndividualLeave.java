package com.kira.leavemanagement.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class IndividualLeave {
    private LocalDate date;  
    private String type;
}
