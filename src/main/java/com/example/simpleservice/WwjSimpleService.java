package com.example.simpleservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class WwjSimpleService extends Service {
    private static final String TAG = "WwjSimpleService";
    private static final String CHANNEL_ID = "WwjServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "========== onCreate() called ==========");
        Log.d(TAG, "Service created at: " + System.currentTimeMillis());
        Log.d(TAG, "Process ID: " + android.os.Process.myPid());
        Log.d(TAG, "Thread ID: " + android.os.Process.myTid());
        
        createNotificationChannel();
        acquireWakeLock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "========== onStartCommand() called ==========");
        Log.d(TAG, "startId: " + startId);
        Log.d(TAG, "flags: " + flags);
        Log.d(TAG, "Service is running in foreground...");
        
        try {
            Notification notification = buildNotification();
            if (notification != null) {
                startForeground(NOTIFICATION_ID, notification);
                Log.d(TAG, "startForeground() succeeded");
            } else {
                Log.e(TAG, "buildNotification() returned null");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "startForeground() failed with SecurityException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "startForeground() failed with Exception: " + e.getMessage());
            e.printStackTrace();
        }
        
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
                        Log.d(TAG, "Process still running, PID: " + android.os.Process.myPid());
                        Log.d(TAG, "WakeLock held: " + (wakeLock != null && wakeLock.isHeld()));
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Thread interrupted");
                        break;
                    }
                }
            }
        }).start();

        return START_STICKY;
    }

    private void acquireWakeLock() {
        try {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null) {
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                wakeLock.acquire();
                Log.d(TAG, "WakeLock acquired successfully");
            } else {
                Log.e(TAG, "PowerManager is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to acquire WakeLock: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.d(TAG, "WakeLock released");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Wwj Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("WwjSimpleService foreground notification");
                channel.setShowBadge(false);
                NotificationManager manager = getSystemService(NotificationManager.class);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created successfully");
                } else {
                    Log.e(TAG, "NotificationManager is null");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to create notification channel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private Notification buildNotification() {
        try {
            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(this, CHANNEL_ID);
            } else {
                builder = new Notification.Builder(this);
            }
            
            Notification notification = builder
                    .setContentTitle("WwjSimpleService")
                    .setContentText("Service is running in foreground")
                    .setSmallIcon(android.R.drawable.ic_menu_info_details)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .build();
            
            Log.d(TAG, "Notification built successfully");
            return notification;
        } catch (Exception e) {
            Log.e(TAG, "Failed to build notification: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "========== onDestroy() called ==========");
        Log.d(TAG, "Service destroyed");
        releaseWakeLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called - Not binding");
        return null;
    }
}
