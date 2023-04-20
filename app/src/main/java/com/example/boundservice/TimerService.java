package com.example.boundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


public class TimerService extends Service {

    public interface TimerServiceCallback {
        void onCounterUpdate(int counter);
    }

    private final IBinder binder = new LocalBinder();
    private Timer timer;
    private Handler handler;
    private int counter = 0;
    private TimerServiceCallback callback;
    public class LocalBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TimerService", "Service started");
        startTimer();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("TimerService", "Service bound");
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        Log.i("TimerService", "Service destroyed");
    }

    public void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                counter++;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("TimerService", "Timer tick " + counter);
                        updateClient(counter);
                        if (counter >= 10) {
                            stopTimer();
                            stopSelf();
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            counter = 0;
        }
    }

    private void updateClient(int counter) {
        // Notify any bound clients of new counter value
        // For example, you could use a callback interface to notify the client
        this.callback.onCounterUpdate(counter);
    }

    public void registerCallback(TimerServiceCallback callback) {
        this.callback = callback;
    }

    public void unregisterCallback() {
        this.callback = null;
    }
}
