package com.aktivo.hamster.data.model.request;

public class UpdateBuildingRequest {
    private String name;
    private String code;
    private String hospitalId;
    private String address;

    public UpdateBuildingRequest(String name, String code, String hospitalId, String address) {
        this.name = name;
        this.code = code;
        this.hospitalId = hospitalId;
        this.address = address;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
