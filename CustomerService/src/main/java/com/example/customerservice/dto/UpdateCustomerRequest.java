package com.example.customerservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCustomerRequest {
    private String firstName;
    private String lastName;

    @Email
    @Size(max = 254)
    private String email;

    @Pattern(regexp = "^(?:\\+?254|0)?[71]\\d{8}$", message = "Invalid Kenyan phone number format")
    private String phone;

    private String address;
}
