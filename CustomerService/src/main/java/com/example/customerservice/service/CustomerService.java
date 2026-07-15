package com.example.customerservice.service;


import com.example.customerservice.dto.CustomerRequest;
import com.example.customerservice.dto.CustomerCreatedResponse;
import lombok.RequiredArgsConstructor;
import com.example.customerservice.mapper.CustomerMapper;
import com.example.customerservice.model.Customer;
import org.springframework.stereotype.Service;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.util.KycStatus;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public CustomerCreatedResponse create(CustomerRequest request) {
        Customer entity = customerMapper.toEntity(request);
        entity.setActive(false);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setRequestFingerprint("abc");
        Customer saved = customerRepository.saveAndFlush(entity);
        return customerMapper.toCreateResponse(saved);
    }
}
