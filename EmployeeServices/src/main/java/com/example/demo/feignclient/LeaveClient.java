package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "LEAVESERVICE", path = "/leave")
public interface LeaveClient {

    @PostMapping("/initialize/{employeeId}")
    void initializeLeaveBalance(@PathVariable("employeeId") int employeeId);
    // client for deleting employee LeaveBalance when employee is deleted 
    @DeleteMapping("/deleteById/{employeeId}")
    void deleteLeavesByEmployee(@PathVariable("employeeId") int employeeId);
}
