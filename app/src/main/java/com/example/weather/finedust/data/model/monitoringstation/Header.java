package com.example.weather.finedust.data.model.monitoringstation;

import com.google.gson.annotations.SerializedName;

public class Header {
    @SerializedName("resultCode")
    private String resultCode;
    @SerializedName("resultMsg")
    private String resultMsg;

    public Header(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
