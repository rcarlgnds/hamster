package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RejectedDataPayload {

    @SerializedName("data")
    private List<AssetRejected> data;

    @SerializedName("pagination")
    private Pagination pagination;

    public List<AssetRejected> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}
