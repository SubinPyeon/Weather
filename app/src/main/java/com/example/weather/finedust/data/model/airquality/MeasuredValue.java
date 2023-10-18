package com.example.weather.finedust.data.model.airquality;

import com.google.gson.annotations.SerializedName;

public class MeasuredValue {
    @SerializedName("coFlag")
    private Object coFlag;
    @SerializedName("coGrade")
    private Grade coGrade;
    @SerializedName("coValue")
    private String coValue;
    @SerializedName("dataTime")
    private String dataTime;
    @SerializedName("khaiGrade")
    private Grade khaiGrade;
    @SerializedName("khaiValue")
    private String khaiValue;
    @SerializedName("mangName")
    private String mangName;
    @SerializedName("no2Flag")
    private Object no2Flag;
    @SerializedName("no2Grade")
    private Grade no2Grade;
    @SerializedName("no2Value")
    private String no2Value;
    @SerializedName("o3Flag")
    private Object o3Flag;
    @SerializedName("o3Grade")
    private Grade o3Grade;
    @SerializedName("o3Value")
    private String o3Value;
    @SerializedName("pm10Flag")
    private Object pm10Flag;
    @SerializedName("pm10Grade")
    private Grade pm10Grade;
    @SerializedName("pm10Grade1h")
    private Grade pm10Grade1h;
    @SerializedName("pm10Value")
    private String pm10Value;
    @SerializedName("pm10Value24")
    private String pm10Value24;
    @SerializedName("pm25Flag")
    private Object pm25Flag;
    @SerializedName("pm25Grade")
    private Grade pm25Grade;
    @SerializedName("pm25Grade1h")
    private Grade pm25Grade1h;
    @SerializedName("pm25Value")
    private String pm25Value;
    @SerializedName("pm25Value24")
    private String pm25Value24;
    @SerializedName("so2Flag")
    private Object so2Flag;
    @SerializedName("so2Grade")
    private Grade so2Grade;
    @SerializedName("so2Value")
    private String so2Value;

    public MeasuredValue(
            Object coFlag, Grade coGrade, String coValue, String dataTime, Grade khaiGrade,
            String khaiValue, String mangName, Object no2Flag, Grade no2Grade, String no2Value,
            Object o3Flag, Grade o3Grade, String o3Value, Object pm10Flag, Grade pm10Grade,
            Grade pm10Grade1h, String pm10Value, String pm10Value24, Object pm25Flag, Grade pm25Grade,
            Grade pm25Grade1h, String pm25Value, String pm25Value24, Object so2Flag, Grade so2Grade, String so2Value) {
        this.coFlag = coFlag;
        this.coGrade = coGrade;
        this.coValue = coValue;
        this.dataTime = dataTime;
        this.khaiGrade = khaiGrade;
        this.khaiValue = khaiValue;
        this.mangName = mangName;
        this.no2Flag = no2Flag;
        this.no2Grade = no2Grade;
        this.no2Value = no2Value;
        this.o3Flag = o3Flag;
        this.o3Grade = o3Grade;
        this.o3Value = o3Value;
        this.pm10Flag = pm10Flag;
        this.pm10Grade = pm10Grade;
        this.pm10Grade1h = pm10Grade1h;
        this.pm10Value = pm10Value;
        this.pm10Value24 = pm10Value24;
        this.pm25Flag = pm25Flag;
        this.pm25Grade = pm25Grade;
        this.pm25Grade1h = pm25Grade1h;
        this.pm25Value = pm25Value;
        this.pm25Value24 = pm25Value24;
        this.so2Flag = so2Flag;
        this.so2Grade = so2Grade;
        this.so2Value = so2Value;
    }

    public Object getCoFlag() {
        return coFlag;
    }

    public Grade getCoGrade() {
        return coGrade;
    }

    public String getCoValue() {
        return coValue;
    }

    public String getDataTime() {
        return dataTime;
    }

    public Grade getKhaiGrade() {
        return khaiGrade;
    }

    public String getKhaiValue() {
        return khaiValue;
    }

    public String getMangName() {
        return mangName;
    }

    public Object getNo2Flag() {
        return no2Flag;
    }

    public Grade getNo2Grade() {
        return no2Grade;
    }

    public String getNo2Value() {
        return no2Value;
    }

    public Object getO3Flag() {
        return o3Flag;
    }

    public Grade getO3Grade() {
        return o3Grade;
    }

    public String getO3Value() {
        return o3Value;
    }

    public Object getPm10Flag() {
        return pm10Flag;
    }

    public Grade getPm10Grade() {
        return pm10Grade;
    }

    public Grade getPm10Grade1h() {
        return pm10Grade1h;
    }

    public String getPm10Value() {
        return pm10Value;
    }

    public String getPm10Value24() {
        return pm10Value24;
    }

    public Object getPm25Flag() {
        return pm25Flag;
    }

    public Grade getPm25Grade() {
        return pm25Grade;
    }

    public Grade getPm25Grade1h() {
        return pm25Grade1h;
    }

    public String getPm25Value() {
        return pm25Value;
    }

    public String getPm25Value24() {
        return pm25Value24;
    }

    public Object getSo2Flag() {
        return so2Flag;
    }

    public Grade getSo2Grade() {
        return so2Grade;
    }

    public String getSo2Value() {
        return so2Value;
    }
}
