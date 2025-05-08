package com.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "EMPLOYEESERVICES", path = "/employees")
public interface Employeeclient {
    @GetMapping("/check/{employeeId}")
    boolean doesEmployeeExist(@PathVariable("employeeId") Integer employeeId);
}
