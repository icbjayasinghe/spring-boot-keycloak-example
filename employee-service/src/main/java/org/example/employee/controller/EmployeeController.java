package org.example.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/anonymous", method = RequestMethod.GET)
    public ResponseEntity<String> getEmployeeForAnonymous() {
        return ResponseEntity.ok("Employee for Anonymous");
    }

    @RolesAllowed("user")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<String> getDepartmentForUser(@RequestHeader String Authorization) {
        final ResponseEntity<String> str = restTemplate.getForEntity("http://localhost:8002/department/user", String.class);
        return ResponseEntity.ok(str.getBody());
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ResponseEntity<String> getEmployeeForAdmin(@RequestHeader String Authorization) {
        return ResponseEntity.ok("Admin Privileged employee");
    }

    @RolesAllowed({"admin", "user"})
    @RequestMapping(value = "/all-user", method = RequestMethod.GET)
    public ResponseEntity<String> getUsers(@RequestHeader String Authorization) {
        return ResponseEntity.ok("Employees for all");
    }

}