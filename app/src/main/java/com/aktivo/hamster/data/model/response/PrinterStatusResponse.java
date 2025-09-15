package com.aktivo.hamster.data.model.response;

public class PrinterStatusResponse {
    private boolean connected;
    private String statusMessage;

    public PrinterStatusResponse(boolean connected, String statusMessage) {
        this.connected = connected;
        this.statusMessage = statusMessage;
    }

    public boolean isConnected() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    public String getStatusMessage() {
        return statusMessage;
    }
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}

