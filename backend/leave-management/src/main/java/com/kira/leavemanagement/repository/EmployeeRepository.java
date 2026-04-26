package com.kira.leavemanagement.repository;
import com.kira.leavemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    Employee findByMail(String mail);
    Employee findByPhoneNumber(String phoneNumber);
}
