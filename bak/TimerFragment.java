package com.syt.health.kitchen.fragment;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimerFragment extends Fragment {

	public final static String TAG = "TimerFragment";

	public static TimerFragment newInstance() {
		TimerFragment f = new TimerFragment();

		return f;
	}

	public TimerFragment() {
	}

	TextView text, text2, text3;
	long starttime = 0;
	static long duration = 0;
	// this posts a message to the main thread from our timertask
	// and updates the textfield
	final Handler h = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			long millis = starttime - System.currentTimeMillis();
			if(millis < 1000){
				timer.cancel();
				timer.purge();
			}
			
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			text.setText(String.format("%d:%02d", minutes, seconds));
			return false;
		}
	});
	// runs without timer be reposting self
	Handler h2 = new Handler();
	Runnable run = new Runnable() {

		@Override
		public void run() {
			long millis = starttime - System.currentTimeMillis();
			if(millis < 1000){
				h2.removeCallbacks(this);
			}
			
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;

			text3.setText(String.format("%d:%02d", minutes, seconds));

			h2.postDelayed(this, 500);
		}
	};

	// tells handler to send a message
	class firstTask extends TimerTask {

		@Override
		public void run() {
			h.sendEmptyMessage(0);
		}
	};

	// tells activity to run on ui thread
	class secondTask extends TimerTask {

		@Override
		public void run() {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					long millis = starttime - System.currentTimeMillis();
					if(millis < 1000){
						timer.cancel();
						timer.purge();
					}
					int seconds = (int) (millis / 1000);
					int minutes = seconds / 60;
					seconds = seconds % 60;

					text2.setText(String.format("%d:%02d", minutes, seconds));
				}
			});
		}
	};

	Timer timer = new Timer();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_timer, container,
				false);

		text = (TextView) v.findViewById(R.id.timerTxt1);
		text2 = (TextView) v.findViewById(R.id.timerTxt2);
		text3 = (TextView) v.findViewById(R.id.timerTxt3);
		
		Button setBtn = (Button) v.findViewById(R.id.setTimerBtn);
		setBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimePicker().show();
			}
			
		});

		Button startBtn = (Button) v.findViewById(R.id.startTimerBtn);
		startBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Button b = (Button) v;
				if(duration == 0) return;
				
				if (b.getText().equals(getString(R.string.stop_timer))) {
					long restTime = (starttime - System.currentTimeMillis());
					if(restTime > 0){
						duration = restTime;
					}
					timer.cancel();
					timer.purge();
					h2.removeCallbacks(run);
					b.setText(getString(R.string.start_timer));
				} else {
					starttime = duration + System.currentTimeMillis();
					timer = new Timer();
					timer.schedule(new firstTask(), 0, 500);
					timer.schedule(new secondTask(), 0, 500);
					h2.postDelayed(run, 0);
					b.setText(getString(R.string.stop_timer));
				}
			}
		});

		return v;
	}
	
	private TimePickerDialog showTimePicker() {

		TimePickerDialog timePicker = new TimePickerDialog(getActivity(),
				new TimePickerDialog.OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						duration = hourOfDay*60*60*1000 + minute*60*1000;
					}
				}, 0, 0,
				true);

		return timePicker;
	}
}
