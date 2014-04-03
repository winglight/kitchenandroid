package com.syt.health.kitchen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.syt.health.kitchen.db.CommonDBOpenHelper;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.fragment.BuyingListFragment;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.utils.Utils;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;

public class StartupActivity extends BaseActivity{	
	private String sid;
	private ImageView note_btn;
	private Button more_btn;
	private Button buying_btn;
	private ProgressDialog pd;
	public static final int NOTE_SUCCESS = 1;
	public static final int HEALTH_SUCCESS = 2;
	public static final int FAILED = 0;
	public static final String CONDITION_KEY = "condition";
	
	private ServiceImpl service;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
     	setContentView(R.layout.activity_home);
     	
     	service = getService();
     	
     	UmengUpdateAgent.update(this);
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		sid = manager.getDeviceId();	
		init();  
	}
	private void init(){
		final SharedPreferences sp =  getSharedPreferences("sp_first",0);
		if(sp.getBoolean(Utils.MAIN_FLAG, true)){
			final ImageView help_iv = (ImageView)findViewById(R.id.activity_home_help_iv);
			final WeakReference<Bitmap>bitmap = Utils.getBitmap(this, R.drawable.help_home);
			help_iv.setImageBitmap(bitmap.get());
			help_iv.setVisibility(View.VISIBLE);
			help_iv.setScaleType(ScaleType.FIT_XY);
			help_iv.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					if(!bitmap.get().isRecycled()){
						bitmap.get().isRecycled();
						System.gc();
					}
					help_iv.setVisibility(View.GONE);
					sp.edit().putBoolean(Utils.MAIN_FLAG, false).commit();
				}
			});
		}
		
		if(sp.getBoolean(Utils.GUIDE_FLAG,true)){		
		}
		note_btn = (ImageView)findViewById(R.id.activity_home_note_btn);
		LinearLayout.LayoutParams note_params = (LinearLayout.LayoutParams)note_btn.getLayoutParams();
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		note_params.width =(int) display.getWidth()*4/5;
		note_params.height =(int)display.getHeight()*3/5;
		note_btn.setLayoutParams(note_params);
		note_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {		
				loginDirect(sid);
			}
		});
		
		more_btn = (Button)findViewById(R.id.activity_home_more_btn);
		more_btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				 loginHealth(sid);
			}
		});
		
		buying_btn = (Button)findViewById(R.id.activity_home_buying_btn);
		buying_btn.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(service.hasFoodList(sid)){
					Intent intent = new Intent();
					intent.setClass(StartupActivity.this, BuyingActivity.class);
					startActivity(intent);
				}else{
					Toast.makeText(StartupActivity.this,getResources().getString(R.string.no_buying), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			
			case NOTE_SUCCESS:
				pd.dismiss();
		
				intent.setClass(StartupActivity.this, NoteActivity.class);
				startActivity(intent);
				finish();
				break;
			case FAILED:
				pd.dismiss();
				Toast.makeText(StartupActivity.this, StartupActivity.this.getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
				break;
			case HEALTH_SUCCESS:
				pd.dismiss();
				intent.setClass(StartupActivity.this, NoteActivity.class);
				intent.putExtra(Utils.HEALTHADVICE, true);
				startActivity(intent);			
			}
		};
	};
	
	//记录SID并验证网络连接状态
	private void loginDirect(final String sid){
		pd = ProgressDialog.show(this,null,getString(R.string.login));
		pd.setCancelable(true);
		new Thread(){
			public void run() {
				Message msg = new Message();
				MessageModel<UserModel> messageModel = new MessageModel<UserModel>();
				messageModel = service.loginDirect(sid);
				if(messageModel.isFlag()){
					msg.what = NOTE_SUCCESS;
				}else{
					msg.what = FAILED;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}
	//记录SID并验证网络连接状态
		private void loginHealth(final String sid){
			pd = ProgressDialog.show(this,null,null);
			
			new Thread(){
				public void run() {
					Message msg = new Message();
					MessageModel<UserModel> messageModel = new MessageModel<UserModel>();
					messageModel = service.loginDirect(sid);
					if(messageModel.isFlag()){
						msg.what = HEALTH_SUCCESS;
					}else{
						msg.what = FAILED;
					}
					handler.sendMessage(msg);
				};
			}.start();
		}
		
}
