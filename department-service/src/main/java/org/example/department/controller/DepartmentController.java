package org.example.department.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @RequestMapping(value = "/anonymous", method = RequestMethod.GET)
    public ResponseEntity<String> getAnonymous() {
        return ResponseEntity.ok("Department for Anonymous");
    }

    @RolesAllowed("user")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public ResponseEntity<String> getUserDepartment(@RequestHeader String Authorization) {
        return ResponseEntity.ok("User Privileged Department");
    }

    @RolesAllowed("admin")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public ResponseEntity<String> getAdminDepartment(@RequestHeader String Authorization) {
        return ResponseEntity.ok("Admin Privileged Department");
    }

    @RolesAllowed({"admin", "user"})
    @RequestMapping(value = "/all-user", method = RequestMethod.GET)
    public ResponseEntity<String> getDepartments(@RequestHeader String Authorization) {
        return ResponseEntity.ok("Departments for all");
    }

}