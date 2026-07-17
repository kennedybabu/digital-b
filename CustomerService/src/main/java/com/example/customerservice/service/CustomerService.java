package com.example.customerservice.service;


import com.example.customerservice.commons.exception.ConflictException;
import com.example.customerservice.commons.exception.ResourceNotFoundException;
import com.example.customerservice.dto.CustomerCreatedResponse;
import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.mapper.CustomerMapper;
import com.example.customerservice.model.Customer;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.util.Fingerprints;
import com.example.customerservice.util.KycStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        }

        //-------Fast path: same emailId already present?
        Optional<Customer> byEmail = customerRepository.findByEmail(request.getEmail());
        if(byEmail.isPresent()) {
            Customer existingCustomer = byEmail.get();
            if(p.equals(existingCustomer.getRequestFingerprint())) {
                return customerMapper.toCreateResponse(existingCustomer);
            }

            throw new ConflictException("Same email address used with different data");
        }

        Customer entity = customerMapper.toEntity(request);
        entity.setActive(false);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setRequestFingerprint("abc");
        Customer saved = customerRepository.saveAndFlush(entity);
        return customerMapper.toCreateResponse(saved);
    }

    public Integer updateKycStatus(String id, String kycStatus) {
        Customer customer = customerRepository.findByExternalId(id).
                orElseThrow(()-> new ResourceNotFoundException("Customer not found with externalId: " + id));

        if("VERIFIED".equalsIgnoreCase(kycStatus)){
            customer.setKycStatus(KycStatus.VERIFIED);
            customer.setActive(true);

            //----create customers login credentials------------

            customerRepository.save(customer);
        } else {
            customer.setKycStatus(KycStatus.REJECTED);
            customerRepository.save(customer);
        }
        return customer.getVersion();
    }
}
