package com.example.hamster.data.model.request;

public class UpdateHospitalRequest {
    private String name;
    private String address;
    private String code;
    private String phone;
    private String email;

    public UpdateHospitalRequest(String name, String address, String code, String phone, String email) {
        this.name = name;
        this.address = address;
        this.code = code;
        this.phone = phone;
        this.email = email;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
