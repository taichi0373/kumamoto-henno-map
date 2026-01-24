package io.github.taichi0373.benefit_map.dto;

public class StoreInfo {
    private Long id;
    private String name;
    private String detail;
    private String telNumber;
    private String licenseStatus;
    private Integer minAge;
    private Integer maxAge;
    private String note;
    private String municipalityName;
    private Double longitude;
    private Double latitude;
    private String address;

    // Default constructor
    public StoreInfo() {}

    // Constructor
    public StoreInfo(Long id, String name, String detail, String telNumber, 
                    String licenseStatus, Integer minAge, Integer maxAge, String note,
                    String municipalityName, Double longitude, Double latitude, String address) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.telNumber = telNumber;
        this.licenseStatus = licenseStatus;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.note = note;
        this.municipalityName = municipalityName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(String licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
