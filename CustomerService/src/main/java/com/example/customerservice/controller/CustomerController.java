package com.example.customerservice.controller;


import com.example.customerservice.dto.*;
import com.example.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/customers")
    public ResponseEntity<CustomerCreatedResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerCreatedResponse body = customerService.create(request);
        URI loc = URI.create("/api/v1/customers/" + body.getExternalId());
        return ResponseEntity.created(loc).body(body);
    }

    @PatchMapping("/customers/{id}/kyc-status")
    public ResponseEntity<Void> updateKycStatus(@PathVariable String id, @RequestBody UpdateKycStatusRequest request) {
        Integer newVersion = customerService.updateKycStatus(id, request.getKycStatus());

        //-----------Return 204 No Content,only ETag
        return ResponseEntity.noContent().eTag("\"" + newVersion + "\"").build();
    }

    @GetMapping("/customers/{externalId}")
    public ResponseEntity<CustomerResponse> getCustomerByExternalId(@PathVariable String externalId) {
        CustomerResponse dto = customerService.getByExternalId(externalId);
        return ResponseEntity.ok().eTag("\"" + dto.getVersion() + "\"").body(dto);
    }

    @GetMapping("/customers/exists")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(customerService.existsByEmail(email));
    }

    @GetMapping("/customers/{externalId}/exists")
    public boolean exists(@PathVariable String externalId) {
        return customerService.exists(externalId);
    }

    @PatchMapping("/customers/{id}")
    public ResponseEntity<Void> updateCustomer(
            @PathVariable String id,
            @RequestHeader(name = "If-Match", required = true) String ifMatch,
            @RequestBody UpdateCustomerRequest request) {
        Integer expected = parseIfMatch(ifMatch);
        Integer newVersion = customerService.updateCustomer(id, request, expected);

        return ResponseEntity.ok().
                eTag("\"" + newVersion + "\"").build();
    }

    private Integer parseIfMatch(String ifMatch) {
        if(ifMatch == null || ifMatch.isBlank()) return null;

        //-----Accept bare numbers (e.g.3) or quoted ("3")------
        String v = ifMatch.replace("\"","").trim();
        return Integer.valueOf(v);
    }
}
