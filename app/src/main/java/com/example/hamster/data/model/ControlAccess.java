package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class ControlAccess {

    @SerializedName("read")
    private boolean read;

    @SerializedName("write")
    private boolean write;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }
}