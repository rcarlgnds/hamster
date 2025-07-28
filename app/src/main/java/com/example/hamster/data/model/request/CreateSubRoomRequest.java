package com.example.hamster.data.model.request;

public class CreateSubRoomRequest {
    private String name;
    private String code;
    private String roomId;
    private String description;

    public CreateSubRoomRequest(String name, String code, String roomId, String description) {
        this.name = name;
        this.code = code;
        this.roomId = roomId;
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
    public String getRoomId() {
        return roomId;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
