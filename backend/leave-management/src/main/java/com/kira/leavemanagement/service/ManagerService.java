package com.kira.leavemanagement.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.kira.leavemanagement.repository.EmployeeRepository;
import com.kira.leavemanagement.repository.LeavesRepository;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.Info;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.LeaveQuota;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.ManagerView.IndividualEmployeeDetails;
import com.kira.leavemanagement.utils.EmployeeHelper;
import com.kira.leavemanagement.response_bodies.LeaveDetails.ManagerView.IndividualLeaveDetails;
import com.kira.leavemanagement.response_bodies.LeaveDetails.ManagerView.LeaveFilter;

import jakarta.transaction.Transactional;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.entity.EmployeeLeaves;
import com.kira.leavemanagement.entity.IndividualDate;
import com.kira.leavemanagement.entity.Leaves;
import com.kira.leavemanagement.exception.UnAuthorizedException;
import com.kira.leavemanagement.repository.EmployeeLeavesRepository;
import com.kira.leavemanagement.request_bodies.ProcessLeave.LeaveFilterReq;
@Service
public class ManagerService {
    private final EmployeeRepository empRepo;
    private final EmployeeLeavesRepository empLeaveRepo;
    private final LeavesRepository leaveRepo;
    @Autowired
    private BCryptPasswordEncoder encoder;

    public ManagerService(EmployeeRepository repo,EmployeeLeavesRepository empLeaveRepo,LeavesRepository leaveRepo){
        this.empRepo=repo;
        this.empLeaveRepo=empLeaveRepo;
        this.leaveRepo=leaveRepo;
    }
    public EmployeeLeaves createEmployeeLeaves(Employee emp,Integer paidDays){
        EmployeeLeaves empLeaves = new EmployeeLeaves();
        empLeaves.setEmployee(emp);
        empLeaves.setRemPaidLeaves(paidDays);
        empLeaves.setAllowedPaidLeaves(paidDays);
        empLeaveRepo.save(empLeaves);
        return empLeaves;
    }

    public List<IndividualEmployeeDetails> getAll(){
        Integer loggedInUserID = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Employee> employees=  empRepo.findByManagerId(loggedInUserID);
        List<IndividualEmployeeDetails> employeeLeavesResponses = new ArrayList<>();
        for(Employee emp:employees){
            EmployeeLeaves employeeLeaves = empLeaveRepo.findById(emp.getId()).orElseThrow();
            employeeLeavesResponses.add(new IndividualEmployeeDetails(emp.getId(), emp.getName(), emp.getMail(), emp.getPhoneNumber(), employeeLeaves.getRemPaidLeaves(), employeeLeaves.getAllowedPaidLeaves()));
        }
        return employeeLeavesResponses;
    }
    @Transactional
    public Info createEmployee(String name,String mail,String password,String phoneNumber,Integer manager_id,Integer paidDays){
        EmployeeHelper.isValidMailAndName(mail,name);
        if(empRepo.findByMail(mail)!=null){
            throw new IllegalArgumentException("Employee with this mail already present");
        }
        EmployeeHelper.isValidPassword(password);
        String hashed_password = encoder.encode(password);
        EmployeeHelper.isValidPhoneNumber(phoneNumber);
        if(empRepo.findByPhoneNumber(phoneNumber)!=null){
            throw new IllegalArgumentException("Employee with this phoneNumber already present");
        }        Employee obj = new Employee(name,mail,hashed_password,phoneNumber,manager_id);
        createEmployeeLeaves(obj, paidDays);
        return new Info(empRepo.save(obj));
    }

    public LeaveQuota editEmployeeLeaves(Integer empId,Integer paidDays){
        EmployeeLeaves empLeaves = empLeaveRepo.findById(empId).orElseThrow(()-> new RuntimeException("No such employee"));
        Employee emp = empRepo.findById(empLeaves.getEmpId()).orElseThrow(()-> new RuntimeException("No such employee"));
        Integer managerId = emp.getManagerId();
        Integer LoggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!managerId.equals(LoggedId)){
            throw new RuntimeException("Only Employees manager can update their team members");
        }
        empLeaves.setAllowedPaidLeaves(paidDays);
        empLeaves.setRemPaidLeaves(paidDays);
        empLeaveRepo.save(empLeaves);
        return new LeaveQuota(paidDays,paidDays);
    }

    public IndividualLeaveDetails getLeaveDetails(Integer leave_id){
        Integer loggedInUserID = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Leaves leave = leaveRepo.findById(leave_id).orElseThrow(()-> new RuntimeException("No leave found"));
        Employee emp = leave.getEmployee();
        Integer managerId = emp.getManagerId();
        if(!loggedInUserID.equals(managerId)){
            throw new UnAuthorizedException("you dont have access to this");
        }
        return new IndividualLeaveDetails(leave);
    }
    public List<LeaveFilter> getLeaves(LeaveFilterReq filter){
        String status = filter.getStatus();
        LocalDate fromDate = filter.getFromDate();
        LocalDate endDate = filter.getEndDate();
        LocalDate appliedFrom = filter.getAppliedFrom();
        LocalDate appliedTo = filter.getAppliedTo();
        Integer empId = filter.getEmpId();
        System.out.println("status "+status+" fromDate "+fromDate+" endDate "+endDate+" appliedFrom "+appliedFrom+" appliedTo "+appliedTo);

        List<Leaves> allLeaves  = new ArrayList<>();
        Specification<Leaves> spec = Specification.unrestricted();

        
        if(empId!=null){
            spec = spec.and((root,q,cb)->cb.equal(root.get("employee").get("id"), empId));
        }
        if(status!=null){
            spec = spec.and((root,q,cb)->cb.equal(root.get("approvalStatus"), status));
        }  
        if(appliedFrom!=null){
            spec = spec.and((root,q,cb)->cb.greaterThanOrEqualTo(root.get("appliedOn"), appliedFrom));
        }
        if(appliedTo!=null){
            spec = spec.and((root,q,cb)->cb.lessThanOrEqualTo(root.get("appliedOn"), appliedTo));
        }
        allLeaves = leaveRepo.findAll(spec);
        if(fromDate!=null){
            List<Leaves> tempLeaves = new ArrayList<>();
            for(Leaves leave : allLeaves){
                List<IndividualDate> dates = leave.getDates();
                LocalDate starDate = dates.get(0).getDate();
                if(starDate.isAfter(fromDate.minusDays(1))){
                    tempLeaves.add(leave);
                }
            }
            allLeaves = tempLeaves; 
        }
        if(endDate!=null){
            List<Leaves> tempLeaves = new ArrayList<>();
            for(Leaves leave : allLeaves){
                List<IndividualDate> dates = leave.getDates();
                LocalDate leaveEndDate = dates.get(dates.size()-1).getDate();
                if(leaveEndDate.isBefore(endDate.plusDays(1))){
                    tempLeaves.add(leave);
                }
            }
            allLeaves = tempLeaves; 
        }
        List<LeaveFilter> res = new ArrayList<>();
        for(Leaves leave:allLeaves){
            res.add(new LeaveFilter(leave));
        }
        return res;
    }
}
