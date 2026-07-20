package com.example.authuser.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class ManagementTokenService {

    @Value("${auth0.mgmt.client-secret")
    private String clientSecret;

    @Value("${auth0.mgmt.client-id")
    private String clientId;

    @Value("${auth0.domain")
    private String domain;

    @Value("${auth0.mgmt.audience")
    private String mgmtAudience;

    private final RestTemplate restTemplate = new RestTemplate();

    private volatile String cachedToken;

    private volatile long expiresAtSec;

    public synchronized String getBearer() {
        long now = System.currentTimeMillis() / 1000L;

        if(cachedToken != null && now <  expiresAtSec - 30) {
            return "Bearer" + cachedToken;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("body", mgmtAudience);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                domain + "/oauth/token",
                new HttpEntity<>(body, headers),
                Map.class
        );

        Map b = response.getBody();
        if(b == null || b.get("access_token") == null) {
            throw  new RuntimeException("Failed to obtain Auth0 management token");
        }

        cachedToken = (String) b.get("access_token");
        Integer expiresIn = (Integer) b.get("expires_in");
        expiresAtSec = now + (expiresIn == null ? 300 : expiresIn);

        return "Bearer " + cachedToken;
    }

}
