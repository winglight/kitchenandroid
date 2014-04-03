package com.syt.health.kitchen.adapter;

import com.syt.health.kitchen.fragment.TodayFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
	private int mSize;
	private String mDate;
	public FragmentPagerAdapter(FragmentManager fm,int size) {
		super(fm);
		mSize = size;
	//	mDate = date;
		// TODO Auto-generated constructor stub
	}
	public void setmDate(String mDate) {
		this.mDate = mDate;
	}
	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		if(arg0 == 0){
			TodayFragment todayFragment = TodayFragment.newInstance();
			Bundle args = new Bundle();
			args.putString("date", mDate);
			todayFragment.setArguments(args);
			return todayFragment;
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSize;
	}
}
