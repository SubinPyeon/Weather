package com.example.weather.finedust.data.model.airquality;

import com.google.gson.annotations.SerializedName;

public class AirQualityResponse {
    @SerializedName("response")
    private Response response;

    public Response getResponse() {
        return response;
    }
}
