package com.example.hamster.data.model.response;

import com.example.hamster.data.model.ActivationDetailData;
import com.google.gson.annotations.SerializedName;

public class ActivationDetailResponse {

    @SerializedName("data")
    private ActivationDetailData data;

    public ActivationDetailData getData() {
        return data;
    }
}