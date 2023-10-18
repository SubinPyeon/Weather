package com.example.weather.finedust.appwidget;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.example.weather.R;
import com.example.weather.finedust.Repository;
import com.example.weather.finedust.data.model.airquality.Grade;
import com.example.weather.finedust.data.model.airquality.MeasuredValue;
import com.example.weather.finedust.data.model.monitoringstation.MonitoringStation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SimpleAirQualityWidgetProvider extends AppWidgetProvider {

//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
//
//        ContextCompat.startForegroundService(
//                context,
//                new Intent(context, UpdateWidgetService.class)
//        );
//    }
//
//    public static class UpdateWidgetService extends LifecycleService {
//
//        private final Executor executor = Executors.newSingleThreadExecutor();
//
//        @Override
//        public void onCreate() {
//            super.onCreate();
//
//            createChannelIfNeeded();
//            startForeground(
//                    NOTIFICATION_ID,
//                    createNotification()
//            );
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.widget_simple);
//                updateViews.setTextViewText(R.id.resultTextView, "권한 없음");
//                updateWidget(updateViews);
//                stopSelf();
//                return super.onStartCommand(intent, flags, startId);
//            }
//
//            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//            Task<Location> lastLocationTask = fusedLocationProviderClient.getLastLocation();
//            lastLocationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location != null) {
//                        executor.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    MonitoringStation nearbyMonitoringStation = Repository.getNearbyMonitoringStation(location.getLatitude(), location.getLongitude());
//                                    MeasuredValue measuredValue = Repository.getLatestAirQualityData(nearbyMonitoringStation.getStationName());
//                                    RemoteViews updateViews = new RemoteViews(getPackageName(), R.layout.widget_simple);
//                                    updateViews.setViewVisibility(R.id.labelTextView, View.VISIBLE);
//                                    updateViews.setViewVisibility(R.id.gradeLabelTextView, View.VISIBLE);
//
//                                    Grade currentGrade = (measuredValue != null) ? measuredValue.getKhaiGrade() : Grade.UNKNOWN;
//                                    updateViews.setTextViewText(R.id.resultTextView, currentGrade.getEmoji());
//                                    updateViews.setTextViewText(R.id.gradeLabelTextView, currentGrade.getLabel());
//
//                                    updateWidget(updateViews);
//                                } catch (Exception exception) {
//                                    exception.printStackTrace();
//                                } finally {
//                                    stopSelf();
//                                }
//                            }
//                        });
//                    }
//                }
//            });
//
//            return super.onStartCommand(intent, flags, startId);
//        }
//
//        @Override
//        public void onDestroy() {
//            super.onDestroy();
//            stopForeground(true);
//        }
//
//        private void createChannelIfNeeded() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                if (notificationManager != null) {
//                    notificationManager.createNotificationChannel(
//                            new NotificationChannel(WIDGET_REFRESH_CHANNEL_ID, "위젯 갱신 채널", NotificationManager.IMPORTANCE_LOW)
//                    );
//                }
//            }
//        }
//
//        private Notification createNotification() {
//            return new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.ic_baseline_refresh_24)
//                    .setChannelId(WIDGET_REFRESH_CHANNEL_ID)
//                    .build();
//        }
//
//        private void updateWidget(RemoteViews updateViews) {
//            ComponentName widgetProvider = new ComponentName(this, SimpleAirQualityWidgetProvider.class);
//            AppWidgetManager.getInstance(this).updateAppWidget(widgetProvider, updateViews);
//        }
//
//        private static final int NOTIFICATION_ID = 12345;
//        private static final String WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH";
//    }
}
