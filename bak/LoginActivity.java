package com.syt.health.kitchen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.syt.health.kitchen.db.CommonDBOpenHelper;
import com.syt.health.kitchen.db.UserDBOpenHelper;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	private Button login_btn;
	private Button register_btn;
	private Button loginDirect_btn;
	private EditText userName_et;
	private EditText userPwd_et;
	private SharedPreferences user_preferences;
	private Intent intent = new Intent();
	private String userName;
	private String userPwd;
	private ProgressDialog pd;
	private String deviceId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);  
		user_preferences = getSharedPreferences("userInfo", 0);
		TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    deviceId = manager.getDeviceId();
		init();   
	}
	/**
	 * Initialization UI
	 */
	private void init(){
		userName = user_preferences.getString("userName", null);
		userPwd = user_preferences.getString("userPwd", null);
		login_btn = (Button)findViewById(R.id.login_login_btn);
		login_btn.setOnClickListener(btn_listener);
		loginDirect_btn = (Button)findViewById(R.id.login_loginDirect_btn);
		userName_et = (EditText)findViewById(R.id.login_user_editText);
		userPwd_et = (EditText)findViewById(R.id.login_password_editText);  
		register_btn = (Button)findViewById(R.id.login_register_btn);
		register_btn.setOnClickListener(btn_listener);
		loginDirect_btn.setOnClickListener(btn_listener);
		if(userName!=null && userPwd!=null){
			//checkUser();
		}
	}
	/**
	 * Settings button listener
	 */
	private OnClickListener btn_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Button btn = (Button)v;
			userName = userName_et.getText().toString();
			userPwd = userPwd_et.getText().toString();
			boolean a,b;
			switch (btn.getId()) {
			
			case R.id.login_login_btn:				
				//intent.setClass(LoginAct.this, MainAct.class);	
				 a = checkUserName(userName);  
				 b = checkPwd(userPwd);
				if(a && b){
					checkUserInfo();
				}
				break;

			case R.id.login_register_btn:
				a = checkUserName(userName);  
				b = checkPwd(userPwd);
				if(a && b){
					commitUserInfo();
				}
				break;	
			case R.id.login_loginDirect_btn:
				loginDirect(deviceId);
			}  
		}
	};
	/**
	 * Local check userName
	 * @param userName
	 * @return
	 */
	private boolean checkUserName(String userName){		
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(userName);
		if(userName.length()==0){
			Toast.makeText(this, "邮箱或手机号码不能为空", 0).show();
			return false;
		}else{
			if(m.matches()){
				if(userName.length()!=11){
					Toast.makeText(this, "请输入正确的手机号码", 0).show();
					return false;
				}
			}else{
				if(!userName.contains("@")||!userName.endsWith(".com")){
					Toast.makeText(this, "请输入正确的邮箱", 0).show();
					return false;  
				}
			}
		}
		return true;  
	}
/**
 * Local check password
 * @param userPwd
 * @return
 */
	private boolean checkPwd(String userPwd){
		if(userPwd.length()<6){
			Toast.makeText(this, "请输入不小于六位的密码", 0).show();
		}
		return true;
	}
	/**
	 * Jump to next activity
	 */
	private void GotoMainActivity(){
		intent.setClass(LoginActivity.this,MainActivity.class);
		finish();  
		startActivity(intent);
	}
	private boolean checkUser(){
		MessageModel<UserModel> mm = new MessageModel<UserModel>();
		if(!userName.contains("@")){
			mm = service.loginByPhone(userName, userPwd);
		}else{
			mm = service.loginByEmail(userName, userPwd);
		}
		if(mm.isFlag()){
			user_preferences.edit().putString("userName", userName).commit();
			user_preferences.edit().putString("userPwd", userPwd).commit();
			GotoMainActivity();
		}else{
			return false;
		}
		return true;
	}
      Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				pd.dismiss();
				Toast.makeText(LoginActivity.this, "success", 0).show();
				break;

			case 1:
				pd.dismiss();
				Toast.makeText(LoginActivity.this, "faild", 0).show();
				break;
			case 3:
				pd.dismiss();
				GotoMainActivity();
			}
			super.handleMessage(msg);
		}		
	};
	private void  loginDirect(final String sid){
		pd = ProgressDialog.show(this, "", "正在登陆.....");
		new Thread(){
			public void run() {
				Message msg = new Message();
				MessageModel<UserModel> messageModel = new MessageModel<UserModel>();
				messageModel = service.loginDirect( sid);
				Log.i("tag", "messageModel.isFlag()="+messageModel.isFlag());
				if(messageModel.isFlag()){
					msg.what = 3;
				}else{
					msg.what = 1;
				}
				handler.sendMessage(msg);
			};
		}.start();
		
	}
/**
 * Server to check user information
 */
	private void checkUserInfo(){
		pd = ProgressDialog.show(this, "","正在验证用户信息,请稍后...");
		new Thread(){
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				if(checkUser()){
					msg.what = 0;
				}else{
					msg.what = 1;
				}
				handler.sendMessage(msg);
			};
		}.start();
		
	}
/**
 * Server to check registered user
 */
	private void commitUserInfo(){
		pd = ProgressDialog.show(this, "", "正在提交用户信息到服务器,请稍后...");
		new Thread(){
			public void run() {
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				UserModel user = new UserModel();
				if(userName.contains("@")){
					user.setEmail(userName);
					user.setPhoneNumber(null);
				}else{
					user.setPhoneNumber(userName);
					user.setEmail(null);
				}
				user.setPassword(userPwd);
				user.setSid(deviceId);
				user_preferences.edit().putString("userName", userName).commit();
				user_preferences.edit().putString("userPwd", userPwd).commit();
				MessageModel message = service.register(user);
				if(message.isFlag()){
					msg.what = 0;
				}else{
					msg.what = 1;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}
}
