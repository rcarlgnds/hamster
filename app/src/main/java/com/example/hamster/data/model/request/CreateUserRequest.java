package com.example.hamster.data.model.request;

public class CreateUserRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String employeeId;
    private String divisionId;
    private String positionId;
    private String hospitalId;
    private String workingUnitId;
    private String roomId;

    public CreateUserRequest(String email, String password, String firstName, String employeeId, String divisionId, String positionId, String hospitalId, String workingUnitId, String roomId) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.employeeId = employeeId;
        this.divisionId = divisionId;
        this.positionId = positionId;
        this.hospitalId = hospitalId;
        this.workingUnitId = workingUnitId;
        this.roomId = roomId;
    }
}