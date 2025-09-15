package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.OptionItem;
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