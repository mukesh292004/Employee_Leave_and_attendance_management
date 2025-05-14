package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ATTENDANCESERVICE", path = "/attendance")
public interface AttendanceClient {
	
    // client for deleting employee attendance when employee is deleted 
    @DeleteMapping("/delete/{employeeId}")
    void deleteAttendancesByEmployee(@PathVariable("employeeId") int employeeId);
}
