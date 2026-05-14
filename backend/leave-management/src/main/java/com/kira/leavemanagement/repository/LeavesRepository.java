package com.kira.leavemanagement.repository;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.entity.Leaves;
@Repository
public interface LeavesRepository extends JpaRepository<Leaves,Integer>,JpaSpecificationExecutor<Leaves> {
    public List<Leaves> findByEmployee(Employee emp);
    public List<Leaves> findByApprovalStatus(String approvalStatus);
}
