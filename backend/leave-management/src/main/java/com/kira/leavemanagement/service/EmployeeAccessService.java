package com.kira.leavemanagement.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.kira.leavemanagement.entity.Employee;
import com.kira.leavemanagement.exception.UnAuthorizedException;
import com.kira.leavemanagement.repository.EmployeeRepository;

@Service
public class EmployeeAccessService {
    private final EmployeeRepository employeeRepository;

    public EmployeeAccessService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void requireSelfOrManagerChain(Integer employeeId, Integer viewerId) {
        if (!canAccess(employeeId, viewerId)) {
            throw new UnAuthorizedException("You do not have access to this employee");
        }
    }

    public void requireManagerChain(Integer employeeId, Integer viewerId) {
        if (!isManagerInChain(employeeId, viewerId)) {
            throw new UnAuthorizedException("Only a manager in the employee's manager chain can perform this action");
        }
    }

    public void requireManager(Integer viewerId) {
        if (employeeRepository.findByManagerId(viewerId).isEmpty()) {
            throw new UnAuthorizedException("Only managers can access team leave");
        }
    }

    public boolean canAccess(Integer employeeId, Integer viewerId) {
        if (employeeId.equals(viewerId)) {
            return true;
        }

        Set<Integer> visited = new HashSet<>();
        Integer managerId = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("No such Employee"))
                .getManagerId();

        while (managerId != null && visited.add(managerId)) {
            if (managerId.equals(viewerId)) {
                return true;
            }
            managerId = employeeRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"))
                    .getManagerId();
        }
        return false;
    }

    private boolean isManagerInChain(Integer employeeId, Integer viewerId) {
        Set<Integer> visited = new HashSet<>();
        Integer managerId = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("No such Employee"))
                .getManagerId();

        while (managerId != null && visited.add(managerId)) {
            if (managerId.equals(viewerId)) {
                return true;
            }
            managerId = employeeRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("Manager not found"))
                    .getManagerId();
        }
        return false;
    }
}
