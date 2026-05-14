package com.kira.leavemanagement.repository;

import org.springframework.stereotype.Repository;

import com.kira.leavemanagement.entity.EmployeeLeaves;

import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface EmployeeLeavesRepository extends JpaRepository<EmployeeLeaves,Integer> {
    
}
