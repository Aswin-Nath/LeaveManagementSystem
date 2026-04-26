package com.kira.leavemanagement.controller;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kira.leavemanagement.service.EmployeeService;
import com.kira.leavemanagement.request_bodies.Updates;
import com.kira.leavemanagement.response_bodies.EmployeeResponse;
import com.kira.leavemanagement.request_bodies.Creates;
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service){
        this.service=service;
    }
    
    @GetMapping
    public List<EmployeeResponse> getAll(){
        return service.getall();
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Integer id){
        return service.get(id);
    }
    @PutMapping("/{id}")
    public EmployeeResponse changeName(@PathVariable Integer id,@RequestBody Updates.UpdateEmployeeName req) {
        return service.editName(id, req.getNewName());
    }
    
    @PostMapping
    public EmployeeResponse createEmployee(@RequestBody Creates.CreateEmployee req){
        return service.createEmployee(req.getName(),req.getMail(),req.getPassword(),req.getPhone_number(),req.getManager_id());
    }
}
