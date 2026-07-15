package com.example.customerservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {
    private String firstName;
    private String lastName;


    @Email
    @NotBlank
    @Size(max=254)
    private String email;

    @Pattern(regexp="^(?:\\+?254|0)?[71]\\d{8}$", message="Invalid Kenyan phone number format")
    private String phone;

    private String address;
    private String externalId;


}
