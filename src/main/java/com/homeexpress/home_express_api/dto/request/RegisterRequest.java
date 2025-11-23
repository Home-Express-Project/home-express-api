package com.homeexpress.home_express_api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Registration payload for public sign up. Added optional district/ward/taxCode
 * so the transport form can submit
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {

    private String username;
    private String email;
    private String phone;
    private String password;
    private String role; // "customer", "transport", "manager"
    private String fullName;
    private String address;
    private String city;
    private String district;
    private String ward;
    private String companyName;
    private String businessLicenseNumber;
    private String taxCode;

}
