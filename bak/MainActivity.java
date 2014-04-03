package com.syt.health.kitchen;



import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
public class MainActivity extends TabActivity implements OnCheckedChangeListener{
	private TabHost mTabHost;
	private RadioGroup  radioGroup;
	private RadioButton home_RadioButton;
	private RadioButton out_RadioButton;
	private RadioButton set_RadioButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub  
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_tabhost);
		init();
    }
	private void init(){  
		radioGroup = (RadioGroup)findViewById(R.id.home_tabhost_radiogroup);
		radioGroup.setOnCheckedChangeListener(this);
		home_RadioButton = (RadioButton)findViewById(R.id.home_tabhost_radiobutton0);
		out_RadioButton = (RadioButton)findViewById(R.id.home_tabhost_radiobutton1);
		set_RadioButton = (RadioButton)findViewById(R.id.home_tabhost_radiobutton2);
		mTabHost = getTabHost();
		mTabHost.addTab(mTabHost.newTabSpec("mHome").setIndicator(getResources().getString(R.string.home_tabhost_rButton0)).setContent(new Intent(this, EatHomeActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("mOut").setIndicator(getResources().getString(R.string.home_tabhost_rButton1)).setContent(new Intent(this, OutSideActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("mSet").setIndicator(getResources().getString(R.string.home_tabhost_rButton2)).setContent(new Intent(this, SettingsActivity.class)));
		mTabHost.setCurrentTab(0);
		home_RadioButton.setTextColor(getResources().getColor(R.color.blue));
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.home_tabhost_radiobutton0:
			mTabHost.setCurrentTab(0);
			home_RadioButton.setTextColor(getResources().getColor(R.color.blue));
			out_RadioButton.setTextColor(getResources().getColor(R.color.white));
			set_RadioButton.setTextColor(getResources().getColor(R.color.white));
			break;

		case R.id.home_tabhost_radiobutton1:
			mTabHost.setCurrentTab(1);
			home_RadioButton.setTextColor(getResources().getColor(R.color.white));
			out_RadioButton.setTextColor(getResources().getColor(R.color.blue));
			set_RadioButton.setTextColor(getResources().getColor(R.color.white));
			break;
		case R.id.home_tabhost_radiobutton2:
			mTabHost.setCurrentTab(2);
			set_RadioButton.setTextColor(getResources().getColor(R.color.blue));
			home_RadioButton.setTextColor(getResources().getColor(R.color.white));
			out_RadioButton.setTextColor(getResources().getColor(R.color.white));
			break;
		}
	}
}
