// File: app/src/main/java/com/example/hamster/data/model/response/UnitResponse.java
package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Pagination;
import com.example.hamster.data.model.Unit;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UnitResponse implements Serializable {

    @SerializedName("data")
    private DataWrapper data;

    public DataWrapper getData() {
        return data;
    }

    public static class DataWrapper implements Serializable {
        @SerializedName("units")
        private List<Unit> units;

        @SerializedName("pagination")
        private Pagination pagination;

        public List<Unit> getUnits() {
            return units;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }
}