package com.syt.health.kitchen;

import java.util.List;

import com.syt.health.kitchen.fragment.BuyingListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class BuyingActivity extends BaseActivity { 
	private boolean isExit;
	
	private List<String> healthList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isExit = true;
		addFragment(BuyingListFragment.newInstance(false), BuyingListFragment.TAG, android.R.id.content);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(isExit){
				this.finish();
				return true;
			}else{
				isExit = true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}
	public List<String> getHealthList() {
		return healthList;
	}
	public void setHealthList(List<String> healthList) {
		this.healthList = healthList;
	}
	
	
}
