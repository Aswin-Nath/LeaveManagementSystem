package com.kira.leavemanagement.entity;

import java.time.LocalDate;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;
import java.util.List;
@Entity
@Data
@Table(name = "leaves")
public class Leaves {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "emp_id", referencedColumnName = "id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "approver_id", referencedColumnName = "id")
    private Employee approver;
    @Column(name = "applied_on")
    private LocalDate appliedOn;

    private String reason;

    @Column(name = "approval_status")
    private String approvalStatus;

    private String comments;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<IndividualDate> dates;
}
