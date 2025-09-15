package com.aktivo.hamster.data.model.request;

public class UpdateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String divisionId;
    private String positionId;


    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }
}