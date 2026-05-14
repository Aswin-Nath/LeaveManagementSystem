package com.kira.leavemanagement.service;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.entity.EmployeeLeaves;
import com.kira.leavemanagement.exception.UnAuthorizedException;
import com.kira.leavemanagement.repository.EmployeeLeavesRepository;
import com.kira.leavemanagement.repository.EmployeeRepository;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.Info;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.LeaveQuota;
import com.kira.leavemanagement.utils.EmployeeHelper;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class EmployeeService {
    private final EmployeeRepository empRepo;
    private final EmployeeLeavesRepository empLeaveRepo;
    @Autowired
    private BCryptPasswordEncoder encoder;

    
    public EmployeeService(EmployeeRepository repo,EmployeeLeavesRepository empLeaveRepo){
        this.empRepo=repo;
        this.empLeaveRepo=empLeaveRepo;
    }



    public Info editName(Integer id,String new_name){
        if(id==null){
            throw new IllegalArgumentException("id argument is not passed");
        }
        Integer loggedInUserID = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("LID "+loggedInUserID+" id "+id);
        if(!loggedInUserID.equals(id)){
            throw new UnAuthorizedException("Forbidden");
        }
        Employee emp = empRepo.findById(id).orElseThrow(()-> new RuntimeException("Employee not found"));
        emp.setName(new_name);
        return new Info(empRepo.save(emp));
    }



    public Info get(Integer empId){

        Integer LoggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee emp = empRepo.findById(empId).orElseThrow(()-> new RuntimeException("No such Employee"));
        if(!LoggedId.equals(empId)){
            throw new UnAuthorizedException("you dont have access to this");
        }
        return new Info(emp);
    }
     
    public LeaveQuota getEmployeeLeaves(Integer empId){
        Integer LoggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!LoggedId.equals(empId)){
            throw new UnAuthorizedException("you dont have access to this");
        }
        EmployeeLeaves employeeLeaves = empLeaveRepo.findById(empId).orElseThrow(()-> new RuntimeException("No such Employee"));
        return new LeaveQuota(employeeLeaves.getAllowedPaidLeaves(),employeeLeaves.getRemPaidLeaves());
    }

    
}
