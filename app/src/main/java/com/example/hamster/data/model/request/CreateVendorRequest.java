package com.example.hamster.data.model.request;

public class CreateVendorRequest {
    private String name;
    private String code;
    private String address;
    private String phone;
    private String email;
    private String description;

    public CreateVendorRequest(String name, String code, String address, String phone, String email, String description) {
        this.name = name;
        this.code = code;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.description = description;
    }

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
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

