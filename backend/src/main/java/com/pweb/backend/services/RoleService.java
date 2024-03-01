package com.pweb.backend.services;

import com.pweb.backend.dao.entities.Role;
import com.pweb.backend.dao.entities.User;
import com.pweb.backend.dao.repositories.RoleRepository;
import com.pweb.backend.dao.repositories.UserRepository;
import com.pweb.backend.requests.AddRoleRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserService userService;


    public RoleService(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }


    public void addRoleToUser(String token, AddRoleRequest addRoleRequest) {
        String email = addRoleRequest.getEmail();

        if (!userRepository.existsByUsername(email)) {
            throw new RuntimeException("Email doesn't exist");
        }
        Optional<User> user = userRepository.findByUsername(email);
        if (user.isEmpty())
            throw new RuntimeException("User not found.");


        List<Role> roles = user.get().getRoles();
        for (Role role : roles) {
            if (role.getName().equals(addRoleRequest.getAction()))
                throw new RuntimeException("Role already added.");
        }

        Role role = new Role();
        role.setName(addRoleRequest.getAction());
        role.setUser(user.get());

        roleRepository.save(role);
    }

}
