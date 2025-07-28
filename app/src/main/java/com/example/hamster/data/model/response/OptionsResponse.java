// File: app/src/main/java/com/example/hamster/data/model/response/OptionsResponse.java
package com.example.hamster.data.model.response;

import com.example.hamster.data.model.OptionItem;
import java.io.Serializable;
import java.util.List;

public class OptionsResponse implements Serializable {
    private boolean success;
    private String message;
    private List<OptionItem> data;

    public List<OptionItem> getData() {
        return data;
    }
}