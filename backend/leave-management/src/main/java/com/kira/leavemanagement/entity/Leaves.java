package com.kira.leavemanagement.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class Leaves {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="emp_id")
    private Integer emp_id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id",name="emp_id")
    private Integer approver_id;

    private LocalDate applied_on;

    private String reason;

    private String approval_status;

    private String comments;

    @Column(columnDefinition = "jsonb")
    private String dates;
}
