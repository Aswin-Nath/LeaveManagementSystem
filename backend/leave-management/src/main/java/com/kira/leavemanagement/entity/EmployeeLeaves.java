package com.kira.leavemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="employee_leaves")
public class EmployeeLeaves {
    @Id
    @Column(name="emp_id")
    private Integer EmpId;

    @Column(name="allowed_paid_leaves")
    private Integer AllowedPaidLeaves;

    @Column(name="used_paid_leaves")
    private Integer UsedPaidLeaves = 0;

    @OneToOne
    @MapsId
    @JoinColumn(name="emp_id",referencedColumnName = "id")
    private Employee employee;

    public EmployeeLeaves(){}
}
