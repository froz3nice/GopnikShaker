package com.opop.brazius.gopnikshaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ShakeDetector.Listener{

    private Context context;
    private WebView wv;
    private MediaPlayer mp;
    private TextView counter;
    ShakeDetector sd;
    SensorManager sensorManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        wv = findViewById(R.id.webView);
        counter = findViewById(R.id.counter);
        counter.setText(String.format("total shakes : %s",PreferenceManager.getDefaultSharedPreferences(context).getLong("counter",0)));
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        loadGopnik();
        wv.setVisibility(View.INVISIBLE);

        mp = MediaPlayer.create(context, R.raw.hardbass);
        mp.setLooping(true);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sd = new ShakeDetector(this);
        sd.start(sensorManager);
    }

    private void loadGopnik(){
        if(mp != null) {
            mp.release();
        }
        Random rnd = new Random();
        switch (rnd.nextInt(6)){
            case 0:
                wv.loadUrl("file:///android_res/raw/gopnik1.gif");
                mp = MediaPlayer.create(context, R.raw.hardbass);
                mp.setLooping(true);
                break;
            case 1:
                wv.loadUrl("file:///android_res/raw/gopnik2.gif");
                mp = MediaPlayer.create(context, R.raw.narkobaron);
                mp.setLooping(true);
                break;
            case 2:
                wv.loadUrl("file:///android_res/raw/gopnik3.gif");
                mp = MediaPlayer.create(context, R.raw.kalbasa);
                mp.setLooping(true);
                break;
            case 3:
                wv.loadUrl("file:///android_res/raw/squater.gif");
                mp = MediaPlayer.create(context, R.raw.bbz);
                mp.setLooping(true);
                break;
            case 4:
                wv.loadUrl("file:///android_res/raw/gif1.gif");
                mp = MediaPlayer.create(context, R.raw.bbz);
                mp.setLooping(true);
                break;
            default:
                wv.loadUrl("file:///android_res/raw/napravlenije.gif");
                mp = MediaPlayer.create(context, R.raw.bbz);
                mp.setLooping(true);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sd.start(sensorManager);
    }

    @Override
    protected void onPause() {
        sd.stop();
        mp.release();
        super.onPause();
    }

    @Override
    public void hearShake() {
        loadGopnik();
        wv.setVisibility(View.VISIBLE);
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong("counter",PreferenceManager.getDefaultSharedPreferences(context).getLong("counter",0)+1).apply();
        counter.setText(String.format("total shakes : %s",PreferenceManager.getDefaultSharedPreferences(context).getLong("counter",0)));
        counter.bringToFront();
        try {
            mp.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
