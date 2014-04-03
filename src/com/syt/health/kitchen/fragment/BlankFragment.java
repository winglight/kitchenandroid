package com.syt.health.kitchen.fragment;

import java.util.Date;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.utils.DateUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BlankFragment extends Fragment{
	private static final String TAG = "BlankFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try{
			View view = inflater.inflate(R.layout.blank, container,false);
			return view;
			}catch(RuntimeException re){
	    		Log.e(TAG, re.getMessage());
	    	}catch(OutOfMemoryError oe){
	    		Log.e(TAG, oe.getMessage());
	    	}
			return new View(getActivity());
	}
}
