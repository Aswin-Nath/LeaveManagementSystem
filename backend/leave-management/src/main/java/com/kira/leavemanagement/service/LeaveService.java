package com.kira.leavemanagement.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.entity.EmployeeLeaves;
import com.kira.leavemanagement.entity.IndividualDate;
import com.kira.leavemanagement.entity.Leaves;
import com.kira.leavemanagement.exception.UnAuthorizedException;
import com.kira.leavemanagement.repository.EmployeeLeavesRepository;
import com.kira.leavemanagement.repository.EmployeeRepository;
import com.kira.leavemanagement.repository.LeavesRepository;
import com.kira.leavemanagement.response_bodies.LeaveDetails.EmployeeView.AllLeave;
import com.kira.leavemanagement.response_bodies.LeaveDetails.EmployeeView.IndividualLeaveDetails;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class LeaveService {
    private final LeavesRepository leaveRepo;
    private final EmployeeRepository empRepo;
    private final EmployeeLeavesRepository empLeaveRepo; 
    public LeaveService(LeavesRepository leaveRepo,EmployeeRepository empRepo,EmployeeLeavesRepository empLeaveRepo){
        this.leaveRepo=leaveRepo;
        this.empRepo = empRepo;
        this.empLeaveRepo = empLeaveRepo;
    }
    
    public String deleteLeave(Integer leave_id){
        Integer loggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Leaves leave = leaveRepo.findById(leave_id).orElseThrow(()-> new RuntimeException("Leave not found"));
        if(!loggedId.equals(leave.getEmployee().getId())){
            throw new UnAuthorizedException("you dont have access to this");
        }
        leaveRepo.delete(leave);
        return "SUCCESS";
    }
    public List<AllLeave> getAllLeaves(){
        Integer loggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee emp = empRepo.findById(loggedId).orElseThrow(()-> new RuntimeException("Employee not found"));
        List<Leaves> leaves = leaveRepo.findByEmployee(emp);

        List<AllLeave> res = new ArrayList<>();

        for(Leaves individualLeave : leaves){
            res.add(new AllLeave(individualLeave));
        }

        return res;
        
    }

    public IndividualLeaveDetails getDetails(Integer leave_id){
        System.out.println(leave_id);
        Integer loggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Leaves leave = leaveRepo.findById(leave_id).orElseThrow(()-> new RuntimeException("Leave doesn't exist"));
        Integer empId = leave.getEmployee().getId();
        System.out.println(leave_id+"leave_id "+loggedId+" logged_id");
        if(!empId.equals(loggedId)){
            throw new UnAuthorizedException("you dont have access to this");
        }
        System.out.println("good");
        return new IndividualLeaveDetails(leave);
    }

    public Integer EditLeave(Integer leave_id,String reason,List<IndividualDate> dates){
        Integer loggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Leaves leave = leaveRepo.findById(leave_id).orElseThrow(()-> new RuntimeException("Leave not found"));
        Employee emp = leave.getEmployee();
        if(!loggedId.equals(emp.getId())){
            throw new UnAuthorizedException("you dont have access to this");
        }
        if(!leave.getApprovalStatus().equals("PENDING")){
            throw new RuntimeException("you cant edit this now");
        }
        if(reason.length()!=0){
            leave.setReason(reason);
        }
        if(dates.size()!=0){
            EmployeeLeaves empLeave = empLeaveRepo.findById(emp.getId()).orElseThrow(()-> new RuntimeException("Quota not found"));
            Integer paid_days = 0;
            for(IndividualDate date : dates){
                Integer ok = 0;
                if(date.getIsPaid()){
                    ok=1;
                }
                paid_days+=ok;
            }
            LocalDate curEndDate = dates.get(dates.size()-1).getDate();
            LocalDate curStartDate = dates.get(0).getDate();
            List<Leaves> empAllLeaves = leaveRepo.findByEmployee(emp);
                    List<LocalDate> invalidDates = new ArrayList<>();
                    for(Leaves individualLeave:empAllLeaves){
                        List<IndividualDate> _dates = individualLeave.getDates();
                        String approval_status = individualLeave.getApprovalStatus();
                        if(!approval_status.equals("REJECTED")){
                            for(IndividualDate date : _dates){
                                LocalDate exactDate = date.getDate();
                                Boolean condition = exactDate.isBefore(curEndDate.plusDays(1)) && exactDate.isAfter(curStartDate.minusDays(1));
                                if(condition){
                                    invalidDates.add(exactDate);
                                }
                            }
                        }
                    }
            if(invalidDates.size()>0){
                throw new RuntimeException("Days are overlapping");
            }
            Integer remainingPaidDays = empLeave.getRemPaidLeaves();
            if(paid_days>remainingPaidDays){
                throw new RuntimeException("requested more paid days than available");
            }
            leave.setDates(dates);
        }
        leaveRepo.save(leave);
        return leave_id;
    }

    public Integer CreateLeave(String reason,List<IndividualDate> dates){
        Integer loggedId = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Employee emp = empRepo.findById(loggedId).orElseThrow(()-> new RuntimeException("Employee not found"));
        Integer paid_days = 0;
        for(IndividualDate date : dates){
            Integer ok = 0;
            if(date.getIsPaid()){
                ok=1;
            }
            paid_days+=ok;
        }
        LocalDate curEndDate = dates.get(dates.size()-1).getDate();
        LocalDate curStartDate = dates.get(0).getDate();
        EmployeeLeaves empLeave = empLeaveRepo.findById(emp.getId()).orElseThrow(()-> new RuntimeException("Quota not found"));
        List<Leaves> empAllLeaves = leaveRepo.findByEmployee(emp);
        List<LocalDate> invalidDates = new ArrayList<>();
        for(Leaves individualLeave:empAllLeaves){
            List<IndividualDate> _dates = individualLeave.getDates();
            String approval_status = individualLeave.getApprovalStatus();
            if(!approval_status.equals("REJECTED")){
                for(IndividualDate date : _dates){
                    LocalDate exactDate = date.getDate();
                    Boolean condition = exactDate.isBefore(curEndDate.plusDays(1)) && exactDate.isAfter(curStartDate.minusDays(1));
                    if(condition){
                        invalidDates.add(exactDate);
                    }
                }
            }
        }
        if(invalidDates.size()>0){
            throw new RuntimeException("Days are overlapping");
        }
        Integer remainingPaidDays = empLeave.getRemPaidLeaves();
        if(paid_days>remainingPaidDays){
            throw new RuntimeException("requested more paid days than available");
        }
        Leaves leave = new Leaves();
        leave.setEmployee(emp);             
        leave.setAppliedOn(LocalDate.now());
        leave.setReason(reason);
        leave.setApprovalStatus("PENDING");
        leave.setComments("");
        leave.setDates(dates);
        leaveRepo.save(leave);
        return leave.getId();
    }
}
