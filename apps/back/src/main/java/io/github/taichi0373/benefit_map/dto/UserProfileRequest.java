package io.github.taichi0373.benefit_map.dto;

import java.time.LocalDate;

public class UserProfileRequest {
    private String username;
    private LocalDate birthDate;
    private String address;
    private String licenseStatus;

    public UserProfileRequest() {}

    public UserProfileRequest(String username, LocalDate birthDate, String address, String licenseStatus) {
        this.username = username;
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
