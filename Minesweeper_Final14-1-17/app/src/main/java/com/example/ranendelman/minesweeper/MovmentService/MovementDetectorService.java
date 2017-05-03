package com.example.ranendelman.minesweeper.MovmentService;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import android.os.Vibrator;

import com.example.ranendelman.minesweeper.R;

/**
 * Created by RanEndelman on 10/01/2017.
 */

public class MovementDetectorService extends Service implements SensorEventListener {
    private final IBinder mBinder = new MyBinder();
    private SensorManager sensorMan;
    private boolean isFirstTime = false;
    private MovementDetectorServiceListener mListener;
    private float firstTime = 0;
    private static final double THRESHOLD = 3;
    private int warningCounter = 0;
    private Long timeNow = 0l;
    private Vibrator vb;

    public class MyBinder extends Binder {
        public MovementDetectorService getService() {
            Log.v("Got Service", "Got Service");
            return MovementDetectorService.this.getSelf();
        }
    }

    public MovementDetectorService getSelf() {
        return this;
    }

    @Override
    public IBinder onBind(Intent intent) {

        sensorMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensorsList = sensorMan.getSensorList(Sensor.TYPE_ALL);

        Log.v("Available sensors: " + sensorsList, "Available sensors: " + sensorsList);

        Sensor sensor = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // null in Genymotion free edition of course

        if (sensor == null && sensorsList.size() > 0) {
            sensor = sensorsList.get(0); // for Genymotion sensors (Genymotion Accelerometer in my case)
        }
        sensorMan.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        vb = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (sensorMan != null) {
            sensorMan.unregisterListener(this);
            sensorMan = null;
        }

        return super.onUnbind(intent);
    }

    /**
     * This method handle the device moving event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[1];

        if (!isFirstTime) {
            firstTime = value;
            isFirstTime = true;
        }

        if (value > firstTime + THRESHOLD || value < firstTime - THRESHOLD) {
            if (System.currentTimeMillis() - timeNow > 2500) {
                timeNow = System.currentTimeMillis();
                warningCounter++;
                Log.v("device moving too much", "device moving too much");
                if (warningCounter < 4)
                    Toast.makeText(this, getString(R.string.movingDeviceToast), Toast.LENGTH_SHORT).show();
                vb.vibrate(200);
                if (warningCounter == 4) {
                    mListener.movementListener();
                    warningCounter = 0;
                }
            }
        }
    }

    /**
     * This method sets the member listener to the specific one
     */
    public void setListener(MovementDetectorServiceListener mListener) {
        this.mListener = mListener;
    }

    /**
     * Unimplemented method
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
