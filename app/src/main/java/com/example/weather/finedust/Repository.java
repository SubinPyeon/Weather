package com.example.weather.finedust;

import android.os.AsyncTask;

import com.example.weather.BuildConfig;
import com.example.weather.finedust.data.model.airquality.AirQualityResponse;
import com.example.weather.finedust.data.model.airquality.MeasuredValue;
import com.example.weather.finedust.data.model.monitoringstation.MonitoringStation;
import com.example.weather.finedust.data.service.AirKoreaApiService;
import com.example.weather.finedust.data.service.KakaoLocalApiService;
import com.example.weather.finedust.data.model.monitoringstation.MonitoringStationsResponse;
import com.example.weather.finedust.data.model.tmcoordinates.Document;
import com.example.weather.finedust.data.model.tmcoordinates.TmCoordinatesResponse;

import java.io.IOException;
import java.util.Comparator;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {

    private static KakaoLocalApiService kakaoLocalApiService =
            new Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create(KakaoLocalApiService.class);

    private static AirKoreaApiService airKoreaApiService =
            new Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create(AirKoreaApiService.class);

//    public static MonitoringStation getNearbyMonitoringStation(double latitude, double longitude) throws IOException {
//        TmCoordinatesResponse tmCoordinatesResponse = kakaoLocalApiService.getTmCoordinates(longitude, latitude).execute().body();
//        if (tmCoordinatesResponse != null && tmCoordinatesResponse.getDocuments() != null && !tmCoordinatesResponse.getDocuments().isEmpty()) {
//            Document tmCoordinates = tmCoordinatesResponse.getDocuments().get(0);
//            Double tmX = tmCoordinates.getX();
//            Double tmY = tmCoordinates.getY();
//
//            if (tmX != null && tmY != null) {
//                MonitoringStationsResponse monitoringStationsResponse = airKoreaApiService.getNearbyMonitoringStation(tmX, tmY).execute().body();
//                if (monitoringStationsResponse != null && monitoringStationsResponse.getResponse() != null
//                        && monitoringStationsResponse.getResponse().getBody() != null && monitoringStationsResponse.getResponse().getBody().getMonitoringStations() != null) {
//                    return monitoringStationsResponse.getResponse().getBody().getMonitoringStations()
//                            .stream()
//                            .min(Comparator.comparingDouble(station -> station.getTm() != null ? station.getTm() : Double.MAX_VALUE))
//                            .orElse(null);
//                }
//            }
//        }
//        return null;
//    }
    public interface NearbyMonitoringStationCallback {
        void onMonitoringStationResult(MonitoringStation station);
    }

    public static void getNearbyMonitoringStation(double latitude, double longitude, NearbyMonitoringStationCallback callback) {
        new NearbyMonitoringStationTask(callback).execute(latitude, longitude);
    }

    private static class NearbyMonitoringStationTask extends AsyncTask<Double, Void, MonitoringStation> {
        private NearbyMonitoringStationCallback callback;

        NearbyMonitoringStationTask(NearbyMonitoringStationCallback callback) {
            this.callback = callback;
        }

        @Override
        protected MonitoringStation doInBackground(Double... params) {
            double latitude = params[0];
            double longitude = params[1];

            try {
                TmCoordinatesResponse tmCoordinatesResponse = kakaoLocalApiService.getTmCoordinates(longitude, latitude).execute().body();
                if (tmCoordinatesResponse != null && tmCoordinatesResponse.getDocuments() != null && !tmCoordinatesResponse.getDocuments().isEmpty()) {
                    Document tmCoordinates = tmCoordinatesResponse.getDocuments().get(0);
                    Double tmX = tmCoordinates.getX();
                    Double tmY = tmCoordinates.getY();

                    if (tmX != null && tmY != null) {
                        MonitoringStationsResponse monitoringStationsResponse = airKoreaApiService.getNearbyMonitoringStation(tmX, tmY).execute().body();
                        if (monitoringStationsResponse != null && monitoringStationsResponse.getResponse() != null
                                && monitoringStationsResponse.getResponse().getBody() != null && monitoringStationsResponse.getResponse().getBody().getMonitoringStations() != null) {
                            return monitoringStationsResponse.getResponse().getBody().getMonitoringStations()
                                    .stream()
                                    .min(Comparator.comparingDouble(station -> station.getTm() != null ? station.getTm() : Double.MAX_VALUE))
                                    .orElse(null);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(MonitoringStation result) {
            callback.onMonitoringStationResult(result);
        }
    }

    public static MeasuredValue getLatestAirQualityData(String stationName) throws IOException {
        AirQualityResponse airQualityResponse = airKoreaApiService.getRealtimeAirQualities(stationName).execute().body();
        if (airQualityResponse != null && airQualityResponse.getResponse() != null
                && airQualityResponse.getResponse().getBody() != null
                && airQualityResponse.getResponse().getBody().getMeasuredValues() != null
                && !airQualityResponse.getResponse().getBody().getMeasuredValues().isEmpty()) {
            return airQualityResponse.getResponse().getBody().getMeasuredValues().get(0);
        }
        return null;
    }

    private static KakaoLocalApiService getKakaoLocalApiService() {
        return new Retrofit.Builder()
                .baseUrl(Url.KAKAO_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(buildHttpClient())
                .build()
                .create(KakaoLocalApiService.class);
    }

    private static AirKoreaApiService getAirKoreaApiService() {
        return new Retrofit.Builder()
                .baseUrl(Url.AIR_KOREA_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(buildHttpClient())
                .build()
                .create(AirKoreaApiService.class);
    }

    private static OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(
                        BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .build();
    }
}
