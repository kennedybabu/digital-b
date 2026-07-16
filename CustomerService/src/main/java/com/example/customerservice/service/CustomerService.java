package com.example.customerservice.service;


import com.example.customerservice.commons.exception.ConflictException;
import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.dto.CustomerCreatedResponse;
import com.example.customerservice.util.Fingerprints;
import lombok.RequiredArgsConstructor;
import com.example.customerservice.mapper.CustomerMapper;
import com.example.customerservice.model.Customer;
import org.springframework.stereotype.Service;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.util.KycStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public CustomerCreatedResponse create(CustomerRequest request) {
        String externalId = request.getExternalId();
        String p = Fingerprints.customerCreate(request.getFirstName(),request.getLastName(),request.getEmail(), request.getPhone(), request.getAddress());

        //Fast path: same externalId already present?
        Optional<Customer> byExternalId = customerRepository.findByExternalId(externalId);
        if(byExternalId.isPresent()) {
            Customer existingCustomer = byExternalId.get();
            if(p.equals(existingCustomer.getRequestFingerprint())) {
                return customerMapper.toCreateResponse(existingCustomer);
            }
            throw new ConflictException("Same externalID used with different data");
//            System.out.println("Conflict discovered. This ExternalID exists");
        }

        Customer entity = customerMapper.toEntity(request);
        entity.setActive(false);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setRequestFingerprint("abc");
        Customer saved = customerRepository.saveAndFlush(entity);
        return customerMapper.toCreateResponse(saved);
    }
}
