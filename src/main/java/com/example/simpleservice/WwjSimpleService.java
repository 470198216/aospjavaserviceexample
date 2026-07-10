package com.example.simpleservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class WwjSimpleService extends Service {
    private static final String TAG = "WwjSimpleService";
    private static final String CHANNEL_ID = "WwjServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "========== onCreate() called ==========");
        Log.d(TAG, "Service created at: " + System.currentTimeMillis());
        Log.d(TAG, "Process ID: " + android.os.Process.myPid());
        Log.d(TAG, "Thread ID: " + android.os.Process.myTid());
        
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "========== onStartCommand() called ==========");
        Log.d(TAG, "startId: " + startId);
        Log.d(TAG, "flags: " + flags);
        Log.d(TAG, "Service is running in foreground...");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        Thread.sleep(3000);
                        count++;
                        Log.d(TAG, "Service running - count: " + count);
                        Log.d(TAG, "Thread still alive in foreground");
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Thread interrupted");
                        break;
                    }
                }
            }
        }).start();

        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Wwj Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("WwjSimpleService foreground notification");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }
        
        return builder
                .setContentTitle("WwjSimpleService")
                .setContentText("Service is running in foreground")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setPriority(Notification.PRIORITY_LOW)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "========== onDestroy() called ==========");
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called - Not binding");
        return null;
    }
}
