package com.kira.leavemanagement.service;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.exception.UnAuthorizedException;
import com.kira.leavemanagement.repository.EmployeeRepository;
import com.kira.leavemanagement.response_bodies.EmployeeResponse;
import com.kira.leavemanagement.utils.EmployeeHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository repo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    
    public EmployeeService(EmployeeRepository repo){
        this.repo=repo;
    }


    public EmployeeResponse editName(Integer id,String new_name){
        if(id==null){
            throw new IllegalArgumentException("id argument is not passed");
        }
        Integer logedInUserID = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("LID "+logedInUserID+" id "+id);
        if(!logedInUserID.equals(id)){
            throw new UnAuthorizedException("Forbidden");
        }
        Employee emp = repo.findById(id).orElseThrow(()-> new RuntimeException("Employee not found"));
        emp.setName(new_name);
        return new EmployeeResponse(repo.save(emp));
    }

    public EmployeeResponse createEmployee(String name,String mail,String password,String phoneNumber,Integer manager_id){
        EmployeeHelper.isValidMailAndName(mail,name);
        if(repo.findByMail(mail)!=null){
            throw new IllegalArgumentException("Employee with this mail already present");
        }
        EmployeeHelper.isValidPassword(password);
        String hashed_password = encoder.encode(password);
        EmployeeHelper.isValidPhoneNumber(phoneNumber);
        if(repo.findByPhoneNumber(phoneNumber)!=null){
            throw new IllegalArgumentException("Employee with this phoneNumber already present");
        }        Employee obj = new Employee(name,mail,hashed_password,phoneNumber,manager_id);
        return new EmployeeResponse(repo.save(obj));
    }

    public List<EmployeeResponse> getall(){
        return repo.findAll().stream().map(emp-> new EmployeeResponse(emp)).toList();
    }

    public EmployeeResponse get(Integer id){
        Employee emp = repo.findById(id).orElseThrow(()-> new RuntimeException("Employee with this id not found"));
        return new EmployeeResponse(emp);
    }
    
}
