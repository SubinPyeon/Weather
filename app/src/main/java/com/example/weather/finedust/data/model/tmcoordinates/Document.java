package com.example.weather.finedust.data.model.tmcoordinates;

import com.google.gson.annotations.SerializedName;

public class Document {
    @SerializedName("x")
    private Double x;

    @SerializedName("y")
    private Double y;

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
