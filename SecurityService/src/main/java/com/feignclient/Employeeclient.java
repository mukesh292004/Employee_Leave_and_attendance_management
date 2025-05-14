package com.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "EMPLOYEESERVICES", path = "/employees")
public interface Employeeclient {
	@GetMapping("/check/{id}/{name}/{email}/{role}")
	boolean doesEmployeeExist(@PathVariable("id") Integer id, @PathVariable("name") String name,
			@PathVariable("email") String email, @PathVariable("role") String role);
}

