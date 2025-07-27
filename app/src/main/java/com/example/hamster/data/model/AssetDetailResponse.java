package com.example.hamster.data.model;

import java.io.Serializable;

public class AssetDetailResponse implements Serializable {
    private Asset data;
    public Asset getData() {
        return data;
    }
}