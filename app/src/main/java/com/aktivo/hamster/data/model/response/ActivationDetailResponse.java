package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.ActivationDetailData;
import com.google.gson.annotations.SerializedName;

public class ActivationDetailResponse {

    @SerializedName("data")
    private ActivationDetailData data;

    public ActivationDetailData getData() {
        return data;
    }
}