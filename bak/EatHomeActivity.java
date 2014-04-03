package com.syt.health.kitchen;

import com.syt.health.kitchen.fragment.TodayFragment;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class EatHomeActivity extends BaseActivity {
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		mContext = this;
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {			
			@Override
			public void onUpdateReturned(int arg0, UpdateResponse arg1) {
				switch (arg0) {
				case 0:
					UmengUpdateAgent.showUpdateDialog(mContext, arg1);
					break;
				case 1:
					 Toast.makeText(mContext,mContext.getResources().getString(R.string.new_version), Toast.LENGTH_LONG)
                     .show();
					break;
				case 2:
					 Toast.makeText(mContext,mContext.getResources().getString(R.string.network_error), Toast.LENGTH_LONG)
                     .show();
					break;
				case 3:
					 Toast.makeText(mContext, mContext.getResources().getString(R.string.connect_timeout), Toast.LENGTH_LONG)
                     .show();
					break;
				}
			}
		});
		addFragment(TodayFragment.newInstance(), TodayFragment.TAG);  
	}
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_BACK){
//			new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.dialog_title)).
//			setMessage(getResources().getString(R.string.dialog_exit_prompt)).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//				
//				@Override  
//				public void onClick(DialogInterface dialog, int which) {
//					finish();			  
//				}
//			}).setNegativeButton("取消", null).show();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
