package com.kira.leavemanagement.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class IndividualDate {
    private LocalDate date;  
    private Boolean isPaid;
}
