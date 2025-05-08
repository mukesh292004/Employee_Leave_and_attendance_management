package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "LEAVESERVICE", path = "/leave")
public interface LeaveClient {

    @GetMapping("/leavescounts/{employeeId}/{month}")
    int getApprovedLeaveCountForMonth(@PathVariable("employeeId") int employeeId, @PathVariable("month") int month);
}
