package com.pweb.backend.requests;

import com.pweb.backend.dao.entities.Role;

public class AddRoleRequest {

    private final String email;
    private final Role.RoleEnum roleEnum;


    public String getEmail() {
        return email;
    }

    public Role.RoleEnum getAction() {
        return roleEnum;
    }

    public AddRoleRequest(String email, Role.RoleEnum roleEnum) {
        this.email = email;
        this.roleEnum = roleEnum;
    }


}
