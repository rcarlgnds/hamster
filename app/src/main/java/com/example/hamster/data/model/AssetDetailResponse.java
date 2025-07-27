package com.example.hamster.data.model;

import java.io.Serializable;

public class AssetDetailResponse implements Serializable {
    private boolean success;
    private Asset data; // Objek data berisi satu Aset

    public boolean isSuccess() {
        return success;
    }

    public Asset getData() {
        return data;
    }
}