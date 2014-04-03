package com.syt.health.kitchen.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.content.Context;
import java.lang.UnsupportedOperationException;

public class ShakeListener implements SensorEventListener {
	private final int FORCE_THRESHOLD;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static int SHAKE_COUNT = 3;

	private SensorManager mSensorMgr;
	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime;
	private OnShakeListener mShakeListener;
	private Context mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;

	public interface OnShakeListener {
		public void onShake();
	}

	public ShakeListener(Context context) {
		mContext = context;

		if ("samsung".equalsIgnoreCase(Build.BRAND)) {
			FORCE_THRESHOLD = 300;
		} else if ("lenovo".equalsIgnoreCase(Build.BRAND)) {
			FORCE_THRESHOLD = 800;
		} else if ("coolpad".equalsIgnoreCase(Build.BRAND)) {
			FORCE_THRESHOLD = 1000;
			SHAKE_COUNT = 1;
		} else if ("zte".equalsIgnoreCase(Build.BRAND)) {
			FORCE_THRESHOLD = 1500;
		} else {
			FORCE_THRESHOLD = 500;
		}

		resume();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		mShakeListener = listener;
	}

	public void resume() {
		mSensorMgr = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorMgr == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}
		boolean supported = mSensorMgr.registerListener(this,
				mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				// 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
				// 根据不同应用，需要的反应速率不同，具体根据实际情况设定
				SensorManager.SENSOR_DELAY_NORMAL);
		if (!supported) {
			mSensorMgr.unregisterListener(this);
//			throw new UnsupportedOperationException(
//					"Accelerometer not supported");
		}
	}

	public void pause() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this);
			mSensorMgr = null;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
		if (sensorType != Sensor.TYPE_ACCELEROMETER)
			return;

		long now = System.currentTimeMillis();

		if ((now - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}

		// values[0]:X轴，values[1]：Y轴，values[2]：Z轴
		float[] values = event.values;

		if ((now - mLastTime) > TIME_THRESHOLD) {
			long diff = now - mLastTime;
			float speed = Math.abs(values[0] + values[1] + values[2] - mLastX
					- mLastY - mLastZ)
					/ diff * 10000;
			if (speed > FORCE_THRESHOLD) {
				if ((++mShakeCount >= SHAKE_COUNT)
						&& (now - mLastShake > SHAKE_DURATION)) {
					mLastShake = now;
					mShakeCount = 0;
					if (mShakeListener != null) {
						mShakeListener.onShake();
					}
				}
				mLastForce = now;
			}
			mLastTime = now;
			mLastX = values[0];
			mLastY = values[1];
			mLastZ = values[2];
		}

	}

}