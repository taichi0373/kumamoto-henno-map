package com.example.back.dto;

import java.time.LocalDate;

public class RegisterRequest {
    private String username;
    private String password;
    private LocalDate birthDate;
    private String address;
    private String licenseStatus;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password, LocalDate birthDate, String address, String licenseStatus) {
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
        this.address = address;
        this.licenseStatus = licenseStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        this.licenseStatus = licenseStatus;
    }
}
