package com.example.authuser.controller;


import com.example.authuser.dto.CreateUserRequest;
import com.example.authuser.service.Auth0UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CreateUserController {

    private final Auth0UserService auth0UserService;

    public CreateUserController(Auth0UserService auth0UserService) {
        this.auth0UserService = auth0UserService;
    }


    @PreAuthorize(("hasAuthority('SCOPE_admin:users.wrtite"))
    @PostMapping("/iam/users")
    public ResponseEntity<Map> createUser(@RequestBody CreateUserRequest request) {
        Map u = auth0UserService.createDbUser(request.getEmail(),  request.getPassword(), request.getCustomerId());
        return ResponseEntity.ok(u);
    }
}
