package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "LEAVESERVICE", path = "/leave")
public interface LeaveClient {

    @PostMapping("/initialize/{employeeId}")
    void initializeLeaveBalance(@PathVariable("employeeId") int employeeId);
}

