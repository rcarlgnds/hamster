package com.aktivo.hamster.data.model.request;

public class CreateAssetActivationSettingRequest {
    private String divisionId;
    private String positionId;
    private int order;
    private boolean isFinalApprover;

    public CreateAssetActivationSettingRequest(String divisionId, String positionId, int order, boolean isFinalApprover) {
        this.divisionId = divisionId;
        this.positionId = positionId;
        this.order = order;
        this.isFinalApprover = isFinalApprover;
    }

    public String getDivisionId() {
        return divisionId;
    }
    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }
    public String getPositionId() {
        return positionId;
    }
    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public boolean isFinalApprover() {
        return isFinalApprover;
    }
    public void setFinalApprover(boolean finalApprover) {
        isFinalApprover = finalApprover;
    }
}

