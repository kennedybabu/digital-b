package com.example.authuser.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class Auth0UserService {

    @Value("${auth0.domain}")
    private String domain;

    private final ManagementTokenService tokenService;
    private final RestTemplate restTemplate = new  RestTemplate();

    public Auth0UserService(ManagementTokenService tokenService) {
        this.tokenService = tokenService;
    }

    public Map createDbUser(String email, String password, String customerId) {
        String auth = tokenService.getBearer();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", auth);

        Map<String, Object> body = new HashMap<>();
        List<String> roles = new ArrayList<String>();

        body.put("email", email);
        body.put("password", password);
        body.put("username", customerId);
        body.put("connection", "Username-Password-Authentication");

        Map<String, Object> appMeta = new HashMap<>();
        appMeta.put("customer_id", customerId);

        body.put("app_meta", appMeta);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                domain + "/api/v2/users",
                new HttpEntity<>(body, headers),
                Map.class
        );

        if(!response.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Auth0 user creation failed: " + response.getStatusCode());
        }

        Map createdUser = response.getBody();
        String userId = (String) createdUser.get("user_id");
        if(userId == null || userId.isBlank()){
            throw new RuntimeException("Auth0 user created but user_id is blank");
        }

        assingRole(userId, "rol_f7OyzRV8MyuzLBZm", auth);

        return response.getBody();
    }

    private void assingRole(String userId, String roleId, String bearer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set("Authorization", bearer);

        Map<String, Object> body = Map.of("roles", List.of(roleId));
        String url = domain + "/api/v2/users/" + userId + "/roles";

        restTemplate.postForEntity(
                url,
                new HttpEntity<>(body, headers),
                Map.class
        );
    }
}
