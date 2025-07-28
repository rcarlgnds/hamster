package com.example.hamster.data.model.request;

public class UpdateRoomRequest {
    private String name;
    private String code;
    private String floorId;
    private String description;

    public UpdateRoomRequest(String name, String code, String floorId, String description) {
        this.name = name;
        this.code = code;
        this.floorId = floorId;
        this.description = description;
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
    public String getFloorId() {
        return floorId;
    }
    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
