package com.example.back.dto;

public class BenefitSearchRequest {
    private Integer age;
    private String licenseStatus;
    private String address;

    public BenefitSearchRequest() {}

    public BenefitSearchRequest(Integer age, String licenseStatus, String address) {
        this.age = age;
        this.licenseStatus = licenseStatus;
        this.address = address;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "BenefitSearchRequest{" +
                "age=" + age +
                ", licenseStatus='" + licenseStatus + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
