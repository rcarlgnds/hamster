package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Building implements Serializable {
    private String id;
    private String name;
    private String code;
    private String hospitalId;
    private Float buildingArea;
    private Float surfaceArea;
    private Integer procurementYear;
    private Date calculationDate;
    private Integer expectationLifetime;
    private Float yearlyDepreciation;
    private Float bookValues;
    private Float residualValues;
    private Integer floorAmount;
    private Date deletedAt;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private Hospital hospital;
    private List<Floor> floors;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public Float getBuildingArea() {
        return buildingArea;
    }

    public Float getSurfaceArea() {
        return surfaceArea;
    }

    public Integer getProcurementYear() {
        return procurementYear;
    }

    public Date getCalculationDate() {
        return calculationDate;
    }

    public Integer getExpectationLifetime() {
        return expectationLifetime;
    }

    public Float getYearlyDepreciation() {
        return yearlyDepreciation;
    }

    public Float getBookValues() {
        return bookValues;
    }

    public Float getResidualValues() {
        return residualValues;
    }

    public Integer getFloorAmount() {
        return floorAmount;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}