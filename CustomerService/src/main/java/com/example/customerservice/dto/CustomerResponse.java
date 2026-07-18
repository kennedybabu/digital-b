package com.example.customerservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CustomerResponse {
   private Integer version;
   private String externalId;
   private String id;
   private String firstName;
   private String lastName;
   private String email;
   private String phone;
   private String address;
   private String active;
   private String createdAt;
   private String updatedAt;
   private String kycStatus;
}
