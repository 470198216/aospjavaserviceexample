package com.example.simpleservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WwjSimpleService extends Service {
    private static final String TAG = "WwjSimpleService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "========== onCreate() called ==========");
        Log.d(TAG, "Service created at: " + System.currentTimeMillis());
        Log.d(TAG, "Process ID: " + android.os.Process.myPid());
        Log.d(TAG, "Thread ID: " + android.os.Process.myTid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "========== onStartCommand() called ==========");
        Log.d(TAG, "startId: " + startId);
        Log.d(TAG, "flags: " + flags);
        Log.d(TAG, "Service is running in background...");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        Thread.sleep(3000);
                        count++;
                        Log.d(TAG, "Service running - count: " + count);
                        Log.d(TAG, "Thread still alive in background");
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Thread interrupted");
                        break;
                    }
                }
            }
        }).start();

        return START_STICKY;
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
