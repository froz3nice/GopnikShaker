package com.opop.brazius.gopnikshaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    /* put this into your activity class */
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private Context context;
    private WebView wv;
    private MediaPlayer mp;
    private TextView counter;

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

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    private void loadGopnik(){
        if(mp != null) {
            mp.release();
        }
        Random rnd = new Random();
        switch (rnd.nextInt(5)){
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
            default:
                wv.loadUrl("file:///android_res/raw/napravlenije.gif");
                mp = MediaPlayer.create(context, R.raw.bbz);
                mp.setLooping(true);
                break;
        }
    }


    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            Log.d("xxx",String.valueOf(x));
            Log.d("yyy",String.valueOf(y));
            Log.d("zzz",String.valueOf(z));

            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 3) {
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

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        mp.release();
        super.onPause();
    }
}
