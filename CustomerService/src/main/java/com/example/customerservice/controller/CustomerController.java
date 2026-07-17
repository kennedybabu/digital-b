package com.example.customerservice.controller;


import com.example.customerservice.dto.CustomerCreatedResponse;
import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.dto.UpdateKycStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.customerservice.service.CustomerService;

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
}
