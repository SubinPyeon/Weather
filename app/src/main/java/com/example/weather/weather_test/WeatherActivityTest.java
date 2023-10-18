package com.example.weather.weather_test;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.weather.R;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class WeatherActivityTest extends AppCompatActivity {

    // GPS 사용을 위한 선언
    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private TextView test_gps;
    private TextView tv_current_temperature, tv_wind, tv_cloud, tv_rain, tv_humidity, tv_total_weather, tv_weather_comment;

    // 좌표값, 날짜, 일출시간, 일몰시간
    private String x = "", y = "", address = "";
    private String date = "", time = "";
    private int sunrise = 800;
    private int sunset = 1800;

    private String weather = "";
    private ImageView iv_weather_back;
    private ImageView weather_image;

    // 날씨에 따른 이미지뷰 선언
    private BitmapDrawable iv_sun, iv_sun_cloud, iv_night, iv_night_cloud, iv_cloud, iv_rain, iv_snow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_test);

        // 네트워크를 별도의 스레드 구현 없이 사용 -> 네트워크작업을 메인 쓰레드에서 동작 = 네트워크 부하가 많을시 속도가 느려질 수 있음
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tv_rain = findViewById(R.id.tv_rain);
        tv_wind = findViewById(R.id.tv_wind);
        tv_cloud = findViewById(R.id.tv_cloud);
        tv_total_weather = findViewById(R.id.tv_total_weather);
        tv_current_temperature = findViewById(R.id.tv_current_temperature);
        weather_image = findViewById(R.id.weather_image);
        tv_total_weather = findViewById(R.id.tv_total_weather);
        tv_humidity = findViewById(R.id.tv_humidity);


        test_gps = findViewById(R.id.test_gps);
        iv_weather_back = findViewById(R.id.iv_weather_back);

        iv_weather_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(); // 인텐트 객체 생성하고
                setResult(RESULT_OK, intent); // 응답 보내기
                finish(); // 현재 액티비티 없애기
            }
        });

        //GPS 사용 구문
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }
        else {
            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(WeatherActivityTest.this);

        double latitude = gpsTracker.getLatitude();  // x좌표
        double longitude = gpsTracker.getLongitude();// y좌표

        String address = getCurrentAddress(latitude, longitude);
        String[] local = address.split(" ");
        // 대한민국 전라북도 군산시 나운2동 -> 나운2동으로 지역 설정
        String localName = local[3];
        String currentaddress = local[1] + " " +  local[2] + " " + local[3];

        test_gps.setText(currentaddress);
        Toast.makeText(WeatherActivityTest.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

//        // assests에 저장된 local_name.xls에서 local[3]에 저장된 ( 나운2동 ) 데이터의 격자x,y 가져오기
//        readExcel(localName);

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        // 날짜, 시간의 형식 설정
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");

        // 현재 날짜를 받아오는 형식 설정 ex) 20221121
        String getDate = simpleDateFormat1.format(mDate);
        // 현재 시간를 받아오는 형식 설정, 시간만 가져오고 WeatherData의 timechange()를 사용하기 위해 시간만 가져오고 뒤에 00을 붙임 ex) 02 + "00"
        String getTime = simpleDateFormat2.format(mDate) + "00";
        String CurrentTime = simpleDateFormat2.format(mDate) + ":00";
        Log.d("date", getDate + getTime);
        // 현재 월 가져오기 봄 = 3월 ~ 5월 / 여름 = 6월 ~ 8월 / 가을 = 9월, 10월 / 겨울 = 11월 ~ 2월
        String getSeason = simpleDateFormat.format(mDate);

        // WeatherData에서 return한 데이터를 가져오는 구문
        WeatherData wd = new WeatherData();
        try {
            date = getDate;  // 새벽이 되면 하루가 지나 2시가 되기 전의 데이터를 써야되는데 날짜가 바뀌어서 그러지못해서 임시로 전날로 정해둠
            time = getTime;
            weather = wd.lookUpWeather(date, time, x, y);
        } catch (IOException e) {
            Log.i("THREE_ERROR1", e.getMessage());
        } catch (JSONException e) {
            Log.i("THREE_ERROR2", e.getMessage());
        }
        Log.d("현재날씨",weather);

        // return한 값을 " " 기준으로 자른 후 배열에 추가
        // array[0] = 구름의 양, array[1] = 강수 확률, array[2] = 기온, array[3] = 풍속, array[4] = 적설량, array[5] = 습도
        String[] weatherarray = weather.split(" ");
        for(int i = 0; i < weatherarray.length; i++) {
            Log.d("weather = ", i + " " + weatherarray[i]);
        }

        tv_cloud.setText("구름 양 : " + weatherarray[0]);
        tv_rain.setText("강수 확률 : " + weatherarray[1]);
        tv_current_temperature.setText(weatherarray[2]);
        tv_wind.setText("풍속 : " + weatherarray[3]);
        tv_humidity.setText("습도 : " + weatherarray[5]);


        tv_total_weather.setText("기준시간 " + CurrentTime);

//        // 날씨에 따른 이미지 세팅
//       if(sunrise <= Integer.valueOf(time) && Integer.valueOf(time) < sunset) {
//            if((weatherarray[0].equals("맑음")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_sun);
//            }
//            else if((weatherarray[0].equals("비")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_rain);
//            }
//            else if((weatherarray[0].equals("구름많음")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_sun_cloud);
//            }
//            else if((weatherarray[0].equals("흐림")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_cloud);
//            }
//            else if(weatherarray[4] != "적설없음") {
//                weather_image.setImageDrawable(iv_snow);
//            }
//        }
//        else {
//            if ((weatherarray[0].equals("맑음")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_night);
//            } else if ((weatherarray[0].equals("비")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_rain);
//            } else if ((weatherarray[0].equals("구름많음")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_night_cloud);
//            } else if ((weatherarray[0].equals("흐림")) && (weatherarray[4].equals("적설없음"))) {
//                weather_image.setImageDrawable(iv_cloud);
//            } else if (weatherarray[4] != "적설없음") {
//                weather_image.setImageDrawable(iv_snow);
//            }
//        }

//        settingImage(getSeason, getTime, weatherarray[2], weatherarray[3]);

    }

//    public void settingImage(String month, String time, String temperature, String wind) {
//        String getMonth = month;
//        String getTime = time;
//        String getTemperature = temperature;
//        String getWind = wind;
//
//        // 각각의 계절의 밤,낮에 맞는 코멘트
//        String spring = "추위가 완전히 가신 봄날씨이므로 상의는 맨투맨이나 셔츠를 하의는 슬렉스나 청바지를 입는것을 추천합니다.";
//        String spring_night = "봄이지만 밤에는 비교적 쌀쌀할 수 있어 외투를 걸치는 것을 추천합니다.";
//        String summer = "한여름의 더위를 주의하시기 바라며 자외선 차단제를 바르고 시원한 옷감의 반팔이나 반바자리를 입는 것을 추천합니다.";
//        String summer_night = "밤에도 온도가 많이 떨어지지 않아 시원하게 입는것을 추천드리며 추위를 잘탄다면 가벼운 외투를 챙기는 것을 추천합니다.";
//        String autumn = "시원한 가을날씨로 바람이 불면 다소 추울 수 있어\n 니트나 후드집업, 코트를 입는것을 추천합니다.";
//        String autumn_night = "밤이 되면 쌀쌀하기 때문에 비교적 두꺼운 재질의 옷을 챙겨\n 따뜻하게 입는 것을 추천합니다.";
//        String winter = "온도가 급격히 떨어지고 바람이 불어 춥기 때문에 후리스같은 따뜻한 옷감의 아우터나 패딩을 입는것을 추천합니다.";
//        String winter_night = "한겨울의 밤은 해가 떨어져 체감온도가 낮아지기 때문에 낮보다 더 따뜻하게 옷을 입는 것을 추천합니다.";
//
//        // 리사이클러뷰를 가로로 설정
//        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//
//        // 코멘트 텍스트뷰 연결
//        tv_weather_comment = findViewById(R.id.tv_weather_comment);
//    }

    // 저장한 엑셀파일 읽어오기
    public void readExcel(String localName) {
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("local_name.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if (wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if (sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal - 1).length;

                    for (int row = rowIndexStart; row < rowTotal; row++) {
                        String contents = sheet.getCell(0, row).getContents();
                        if (contents.contains(localName)) {
                            x = sheet.getCell(1, row).getContents();
                            y = sheet.getCell(2, row).getContents();
                            row = rowTotal;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.i("READ_EXCEL1", e.getMessage());
            e.printStackTrace();
        } catch (BiffException e) {
            Log.i("READ_EXCEL1", e.getMessage());
            e.printStackTrace();
        }
        // x, y = String형 전역변수
        Log.i("격자값", "x = " + x + "  y = " + y);
    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료
                // 2 가지 경우
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(WeatherActivityTest.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(WeatherActivityTest.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(WeatherActivityTest.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(WeatherActivityTest.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(WeatherActivityTest.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(WeatherActivityTest.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(WeatherActivityTest.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);

            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(WeatherActivityTest.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivityTest.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public class weatherViewHolder {
    }
}
