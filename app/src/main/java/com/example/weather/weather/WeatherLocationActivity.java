package com.example.weather.weather;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.weather.util.Conversion;
import com.example.weather.weather.util.RequestPermission;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class WeatherLocationActivity extends AppCompatActivity {

    private RecyclerView weatherRecyclerView;
    private TextView locationText;

    private Point curPoint;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_location);

        TextView tvDate = findViewById(R.id.tv_date);
        Button btnRefresh = findViewById(R.id.btn_refresh);
        locationText = findViewById(R.id.tv_location);
        weatherRecyclerView = findViewById(R.id.rv_weather);


        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvDate.setText(new SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().getTime()) + "날씨");

        // 위치 권한 요청
        //requestLocationPermission();

        btnRefresh.setOnClickListener(v -> {
            //getLocationAndFetchWeather();
            requestLocation();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new RequestPermission(this).requestLocation(); // 위치 권한 요청
    }

    private void setWeather(String nx, String ny) {
        Calendar cal = Calendar.getInstance();
        String base_date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.getTime());
        String timeH = new SimpleDateFormat("HH", Locale.getDefault()).format(cal.getTime());
        String timeM = new SimpleDateFormat("HH", Locale.getDefault()).format(cal.getTime());
        String base_time = getBaseTime(timeH, timeM);
        if (timeH.equals("00") && base_time.equals("2330")) {
            cal.add(Calendar.DATE, -1);
            base_date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.getTime());
        }

        Call<WEATHER> call = ApiObject.retrofitService.GetWeather(60, 1, "JSON", base_date, base_time, nx, ny);

        call.enqueue(new retrofit2.Callback<WEATHER>() {
            @Override
            public void onResponse(@NonNull Call<WEATHER> call, @NonNull Response<WEATHER> response) {
                if (response.isSuccessful()) {
                    List<ITEM> items = Objects.requireNonNull(response.body()).response.body.items.item;

                    ModelWeather[] weatherArr = new ModelWeather[6];

                    for (int i = 0; i < 6; i++) {
                        weatherArr[i] = new ModelWeather();
                    }

                    int index = 0;
                    int totalCount = response.body().response.body.totalCount - 1;
                    for (int i = 0; i <= totalCount; i++) {
                        index %= 6;
                        switch (items.get(i).category) {
                            case "PTY":
                                weatherArr[index].setRainType(items.get(i).fcstValue);
                                break;
                            case "REH":
                                weatherArr[index].setHumidity(items.get(i).fcstValue);
                                break;
                            case "SKY":
                                weatherArr[index].setSky(items.get(i).fcstValue);
                                break;
                            case "T1H":
                                weatherArr[index].setTemp(items.get(i).fcstValue);
                                break;
                            default:
                                break;
                        }
                        index++;
                    }

                    for (int i = 0; i <= 5; i++) {
                        weatherArr[i].setFcstTime(items.get(i).fcstTime);
                    }

                    weatherRecyclerView.setAdapter(new WeatherAdapter(weatherArr));

                    Toast.makeText(getApplicationContext(), items.get(0).fcstDate + ", " + items.get(0).fcstTime + "의 날씨 정보입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<WEATHER> call, @NonNull Throwable t) {
                TextView tvError = findViewById(R.id.tvError);
                tvError.setText("api fail : " + t.getMessage() + "\n 다시 시도해주세요.");
                tvError.setVisibility(View.VISIBLE);
                Log.d("api fail", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    private void requestLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation()
            .addOnSuccessListener(location -> {
                if (location != null) {
                    curPoint = Conversion.dfs_xy_conv(location.getLatitude(), location.getLongitude());
                    setWeather(String.valueOf(curPoint.x), String.valueOf(curPoint.y));
                    List<Address> addressList = getAddress(location.getLatitude(), location.getLongitude());
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);

                        locationText.setText(location.getLatitude() + ", " + location.getLongitude() + "\n" + address.getAdminArea() + " " + address.getLocality() + " " + address.getThoroughfare());
                    } else {
                        locationText.setText(location.getLatitude() + ", " + location.getLongitude());
                    }
                }
            })
            .addOnFailureListener(e -> Log.e("requestLocation", "fail"));
    }

    private List<Address> getAddress(double lat, double lng) {
        List<Address> address = null;

        try {
            Geocoder geocoder = new Geocoder(this, Locale.KOREA);
            address = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            Toast.makeText(this, "주소를 가져 올 수 없습니다", Toast.LENGTH_SHORT).show();
        }

        return address;
    }

    private String getBaseTime(String h, String m) {
        String result;

        if (Integer.parseInt(m) < 45) {
            if (h.equals("00")) {
                result = "2330";
            } else {
                int resultH = Integer.parseInt(h) - 1;
                if (resultH < 10) {
                    result = "0" + resultH + "30";
                } else {
                    result = resultH + "30";
                }
            }
        } else {
            result = h + "30";
        }

        return result;
    }
}
