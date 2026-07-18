package com.example.customerservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateKycStatusRequest {

    @JsonProperty("kyc-status")
    private String kycStatus;
}
