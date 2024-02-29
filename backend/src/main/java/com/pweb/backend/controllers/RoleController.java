package com.pweb.backend.controllers;

import com.pweb.backend.dao.entities.Role;
import com.pweb.backend.requests.AddRoleRequest;
import com.pweb.backend.services.RoleService;
import com.pweb.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;
    private final UserService userService;

    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @PostMapping("/add-role")
    @Operation(summary = "Add role to user")
    public String addRole(@RequestBody AddRoleRequest addRoleRequest,@RequestHeader("Authorization") String token){
        roleService.addRoleToUser(token, addRoleRequest);
        return "succes";

    }

    @GetMapping("/my-roles")
    @Operation(summary = "List roles")
    public List<Role.RoleEnum> listRoles(@RequestHeader("Authorization") String token){
        return roleService.listActions(token);
    }
}
