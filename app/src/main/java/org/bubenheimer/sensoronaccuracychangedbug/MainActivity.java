package org.bubenheimer.sensoronaccuracychangedbug;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class MainActivity extends Activity implements SensorEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SensorManager sensorManager;
    private Sensor sensor;

    private int totalAccuracyChanges;
    private int falseAccuracyChanges;

    private int lastAccuracy;

    private long lastLog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = getSystemService(SensorManager.class);
        if (sensorManager == null) {
            throw new AssertionError("No sensor manager");
        }
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    protected void onStart() {
        super.onStart();

        totalAccuracyChanges = 0;
        falseAccuracyChanges = 0;
        lastAccuracy = -1;
        lastLog = Long.MIN_VALUE;

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);

        super.onStop();
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
//        Log.d(TAG, "onAccuracyChanged(" + sensor + "," + accuracy + ")");

        ++totalAccuracyChanges;
        if (lastAccuracy == accuracy) {
            ++falseAccuracyChanges;
            lastAccuracy = accuracy;
        }

        if (shouldLog()) {
            Log.i(TAG, "Total accuracy changes: " + totalAccuracyChanges
                    + "   False accuracy changes: " + falseAccuracyChanges);
        }
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
//        Log.v(TAG, "onSensorChanged(" + event + ")");
    }

    private boolean shouldLog() {
        final long time = SystemClock.elapsedRealtime();
        if (time > lastLog + 60_000L) {
            lastLog = time;
            return true;
        } else {
            return false;
        }
    }
}
