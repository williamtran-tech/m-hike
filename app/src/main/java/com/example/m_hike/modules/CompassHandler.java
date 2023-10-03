package com.example.m_hike.modules;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassHandler implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    private boolean isLastAccelerometer = true;
    private boolean isLastMagnetometer = true;
    private float currentDegree = 0f;
    private long lastUpdatedTime = 0;
    private TextView compassValue;
    private ImageView compass;

    public CompassHandler(Context context, TextView compassValue, ImageView compass) {
        this.compassValue = compassValue;
        this.compass = compass;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, magnetometer);
    }

    public float getCurrentDegree() {
        return currentDegree;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setCurrentDegree(float currentDegree) {
        this.currentDegree = currentDegree;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == accelerometer) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            isLastAccelerometer = true;
        } else if(sensorEvent.sensor == magnetometer) {
            System.arraycopy(sensorEvent.values, 0, lastMagnetometer, 0, sensorEvent.values.length);
            isLastMagnetometer = true;
        }

        if (isLastAccelerometer && isLastMagnetometer) {
            if (System.currentTimeMillis() - lastUpdatedTime > 15) {

                SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
                SensorManager.getOrientation(rotationMatrix, orientation);

                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float) (Math.toDegrees(azimuthInRadians) + 360) % 360;

                RotateAnimation rotateAnimation = new RotateAnimation(
                        currentDegree,
                        -azimuthInDegrees,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                rotateAnimation.setDuration(50);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new LinearInterpolator());
                compass.startAnimation(rotateAnimation);

                currentDegree = -azimuthInDegrees;
                lastUpdatedTime = System.currentTimeMillis();

                int x = (int) azimuthInDegrees;
                compassValue.setText(x + "°");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
