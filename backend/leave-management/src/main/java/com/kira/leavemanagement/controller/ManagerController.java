package com.kira.leavemanagement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kira.leavemanagement.entity.Leaves;
import com.kira.leavemanagement.request_bodies.EmployeeRequest.CreateEmployee;
import com.kira.leavemanagement.request_bodies.EmployeeRequest.UpdateQuota;
import com.kira.leavemanagement.request_bodies.ProcessLeave.LeaveFilterReq;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.Info;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.LeaveQuota;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.ManagerView.IndividualEmployeeDetails;
import com.kira.leavemanagement.response_bodies.LeaveDetails.ManagerView.IndividualLeaveDetails;
import com.kira.leavemanagement.response_bodies.LeaveDetails.ManagerView.LeaveFilter;
import com.kira.leavemanagement.service.EmployeeService;
import com.kira.leavemanagement.service.ManagerService;


@RestController
@RequestMapping("/manager")
public class ManagerController {
    private final ManagerService managerService;
    private final EmployeeService empService;
    public ManagerController(ManagerService service,EmployeeService employeeService){
        this.managerService=service;
        this.empService=employeeService;
    }
    @GetMapping("/employees")
    public List<IndividualEmployeeDetails> getAll(){
        return managerService.getAll();
    }
    @PostMapping("/create")
    public Info createEmployee(@RequestBody CreateEmployee req){
        return  managerService.createEmployee(req.getName(),req.getMail(),req.getPassword(),req.getPhone_number(),req.getManager_id(),req.getAllowed_paid_days());
    }

    @PutMapping("/days/{empId}")
    public LeaveQuota editEmployeeLeaves(@PathVariable Integer empId,@RequestBody UpdateQuota req){
        return managerService.editEmployeeLeaves(empId, req.getPaidDays());
    }
    @GetMapping("/leave/{leave_id}")
    public IndividualLeaveDetails getLeaveDetails(@PathVariable Integer leave_id){
        return managerService.getLeaveDetails(leave_id);
    }
    @GetMapping("/leave")
    public List<LeaveFilter> getLeaves(@ModelAttribute LeaveFilterReq filter){
        return managerService.getLeaves(filter);
    }
}
