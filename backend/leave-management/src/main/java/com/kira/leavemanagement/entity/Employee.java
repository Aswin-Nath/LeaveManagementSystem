package com.kira.leavemanagement.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name="employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String mail;
    private String hashed_password;
    @Column(name="phone_number")
    private String phoneNumber;
    @Column(name = "manager_id")
    private Integer managerId;
    private Integer session_version=0;
    public Employee(){};
    public Employee(String empName,String mail,String hashed_password,String phone_number,Integer manager_id){
        this.name=empName;
        this.mail=mail;
        this.hashed_password=hashed_password;
        this.phoneNumber=phone_number;
        this.managerId=manager_id;
        this.session_version=0;
    }
    
}
