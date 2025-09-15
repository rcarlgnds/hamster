package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApprovalDataPayload {

    @SerializedName("data")
    private List<ApprovalItem> data;

    @SerializedName("pagination")
    private Pagination pagination;

    public List<ApprovalItem> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }
}