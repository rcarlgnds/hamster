package com.example.hamster.data.model.request;

import java.util.List;

public class PrintLabelRequest {
    private List<String> assetCodes;
    private String templateId;

    public PrintLabelRequest(List<String> assetCodes, String templateId) {
        this.assetCodes = assetCodes;
        this.templateId = templateId;
    }

    public List<String> getAssetCodes() {
        return assetCodes;
    }
    public void setAssetCodes(List<String> assetCodes) {
        this.assetCodes = assetCodes;
    }
    public String getTemplateId() {
        return templateId;
    }
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}

