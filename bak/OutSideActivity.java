package com.syt.health.kitchen;

import java.util.List;

import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.syt.health.kitchen.db.UserDBOpenHelper;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.fragment.HealthConditionSettingsFragment;
import com.syt.health.kitchen.fragment.OutSideFragment;
import com.syt.health.kitchen.service.param.SmartMealConditionParam;
import com.syt.health.kitchen.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;


public class OutSideActivity extends BaseActivity {
	private final static String TAG = "OutSideActivity";
	
	public final static String IS_HOME_EXTRA_DATA = "isHome";
	
	private boolean isHome;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);
        
        //Get current user
        UserModel user = getService().getCurrentUser();
        
        if(user == null){
        	toastMsg(R.string.no_login_prompt);
        	return;
        }
        
        SmartMealConditionParam params = user.getObjOutParams();	

        addFragment(OutSideFragment.newInstance(params), TAG);
        
	}
	
	public List<HealthCondClassifyModel> getConditionList(){
		return service.getAllHealthCondition();
	}
	
	public void saveParams(SmartMealConditionParam params){
		if(isHome){
			service.saveSmartParams(params);
		}else{
			service.saveOutParams(params);
		}
		toastMsg(R.string.save_smart_condition);
		
		this.finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// We do nothing here. We're only handling this to keep orientation
		// or keyboard hiding from causing the WebView activity to restart.
	}
	
	public void toastMsg(int resId) {
		final String msg = this.getString(resId);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(OutSideActivity.this, msg, Toast.LENGTH_LONG)
						.show();
			}
		});
	}
}
