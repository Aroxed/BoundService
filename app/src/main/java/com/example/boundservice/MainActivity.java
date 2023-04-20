package com.example.boundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.boundservice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private boolean isBound = false;
    private TextView counterTextView;
    private TimerService timerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.boundservice.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        counterTextView = findViewById(R.id.counterTextView);

    }


    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TimerService.LocalBinder binder = (TimerService.LocalBinder) iBinder;
            timerService = binder.getService();
            timerService.registerCallback(callback);
            isBound = true;
            timerService.startTimer();
            Log.i("TimerClientActivity", "Service connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
            Log.i("TimerClientActivity", "Service disconnected");
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            timerService.unregisterCallback();
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    public void updateCounterTextView(final int counter) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                counterTextView.setText("Counter: " + counter);
            }
        });
    }

    private TimerService.TimerServiceCallback callback = new TimerService.TimerServiceCallback() {
        @Override
        public void onCounterUpdate(int counter) {
            updateCounterTextView(counter);
        }
    };
}