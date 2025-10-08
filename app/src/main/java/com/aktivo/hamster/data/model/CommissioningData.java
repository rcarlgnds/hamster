package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class CommissioningData {
    @SerializedName("installationTransaction")
    private TransactionDetails installationTransaction;

    @SerializedName("trainingTransaction")
    private TransactionDetails trainingTransaction;

    public TransactionDetails getInstallationTransaction() {
        return installationTransaction;
    }

    public TransactionDetails getTrainingTransaction() {
        return trainingTransaction;
    }

    public boolean isCommissioned() {
        return installationTransaction != null || trainingTransaction != null;
    }
}