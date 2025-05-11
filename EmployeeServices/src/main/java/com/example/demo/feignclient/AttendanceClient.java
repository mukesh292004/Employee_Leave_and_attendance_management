package com.example.demo.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ATTENDANCESERVICE", path = "/attendance")
public interface AttendanceClient {
    @PostMapping("/initialize/{employeeId}")
    void initializeLeaveBalance(@PathVariable("employeeId") int employeeId);

    @DeleteMapping("/delete/{employeeId}")
    void deleteAttendancesByEmployee(@PathVariable("employeeId") int employeeId);
}
