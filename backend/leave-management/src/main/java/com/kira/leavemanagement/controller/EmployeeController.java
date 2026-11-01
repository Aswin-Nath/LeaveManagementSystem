package com.kira.leavemanagement.controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kira.leavemanagement.service.EmployeeService;
import com.kira.leavemanagement.service.LeaveService;


import com.kira.leavemanagement.request_bodies.EmployeeRequest.UpdateName;
import com.kira.leavemanagement.request_bodies.LeaveRequest.Create;
import com.kira.leavemanagement.request_bodies.LeaveRequest.Update;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.Info;
import com.kira.leavemanagement.response_bodies.EmployeeDetails.EmployeeView.LeaveQuota;
import com.kira.leavemanagement.response_bodies.LeaveDetails.EmployeeView.AllLeave;
import com.kira.leavemanagement.response_bodies.LeaveDetails.EmployeeView.IndividualLeaveDetails;

import java.util.List;
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService empService;
    private final LeaveService leaveService;
    public EmployeeController(EmployeeService service,LeaveService leaveService){
        this.empService=service;
        this.leaveService=leaveService;
    }
    
    @GetMapping("/days/{empId}")
    public LeaveQuota getEmployeeLeaves(@PathVariable Integer empId){
        return empService.getEmployeeLeaves(empId);
    }
    @GetMapping("/{empId}")
    public Info get(@PathVariable Integer empId){
        return empService.get(empId);
    }
    @PutMapping("/{empId}")
    public Info changeName(@PathVariable Integer empId,@RequestBody UpdateName req) {
        return empService.editName(empId, req.getNewName());
    }
    @PostMapping("/leave")
    public Integer applyLeave(@RequestBody Create req){
        return leaveService.CreateLeave(req.getReason(), req.getDates());
    }
    @GetMapping("/leave")
    public List<AllLeave>  getAllLeaves(){
        return leaveService.getAllLeaves();
    }
    @GetMapping("/leave/{leave_id}")
    public IndividualLeaveDetails getLeave(@PathVariable Integer leave_id){
        System.out.println(leave_id);
        return leaveService.getDetails(leave_id);
    }
    @DeleteMapping("/leave/{leave_id}")
    public String  deleteLeave(@PathVariable Integer leave_id){
        return leaveService.deleteLeave(leave_id);
    }
    @PutMapping("/leave/{leave_id}")
    public Integer updateLeave(@PathVariable Integer leave_id,@RequestBody Update req){
        return leaveService.EditLeave(leave_id, req.getReason(), req.getDates());
    }
    @PutMapping("/leave/{leave_id}/withdraw")
    public String withdrawLeave(@PathVariable Integer leave_id){
        return leaveService.withdrawLeave(leave_id);
    }
    
}
