package com.syt.health.kitchen;

import com.syt.health.kitchen.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class LaunchActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.launch_bg);
		iv.setScaleType(ScaleType.FIT_XY);
		setContentView(iv);
		final SharedPreferences sp =  getSharedPreferences("sp_first",0);
		new Handler().postDelayed(new Runnable() {			
			@Override
			public void run() {
				Intent intent;
				if(sp.getBoolean(Utils.GUIDE_FLAG, true)){
					sp.edit().putBoolean(Utils.GUIDE_FLAG, false).commit();
					intent = new Intent(LaunchActivity.this,GuideActivity.class);
					startActivity(intent);
					finish();
				}else{
					intent = new Intent(LaunchActivity.this,StartupActivity.class);
					startActivity(intent);
					finish();
				}							
			}
		}, 1000);
	}
	@Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
