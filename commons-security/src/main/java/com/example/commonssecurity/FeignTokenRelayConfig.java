package com.example.commonssecurity;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

@Configuration
public class FeignTokenRelayConfig {

    @Value("${auth0.audience}")
    private String expectedAudience;

    @Bean
    public RequestInterceptor relayUserJwt() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if(authentication instanceof JwtAuthenticationToken) {
                    Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();

                    Object audience = jwt.getClaims().get("audience");

                    if( audience instanceof List && ((List<?>) audience).contains(expectedAudience)) {
                        requestTemplate.header("Authorization", "Bearer " + jwt.getTokenValue());
                    }
                }
            }
        };
    }
}
