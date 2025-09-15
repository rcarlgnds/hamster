package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.UnreadCountData;

public class UnreadCountResponse {
    private boolean success;
    private String message;
    private UnreadCountData data;
    private long timestamp;

    public UnreadCountData getData() {
        return data;
    }
}