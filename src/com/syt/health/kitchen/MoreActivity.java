package com.syt.health.kitchen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.syt.health.kitchen.fragment.WebFragment;

import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MoreActivity extends BaseActivity {
	
	private boolean isShowTips = true;
	
	@Override     
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);   
		setContentView(R.layout.activity_more);		
		String[]datas = getResources().getStringArray(R.array.more);
		int [] icons ={R.drawable.user_guide_cell,R.drawable.advice_cell,R.drawable.share_cell,
				R.drawable.clear_cache_cell,R.drawable.activity_cell,R.drawable.about_us_cell,R.drawable.terms_cell};
		ListView lv =(ListView)findViewById(R.id.activity_more_lv);
		lv.setAdapter(new ListAdapter(datas,icons));		
		final FeedBackListener listener = new FeedBackListener() {		
			@Override
			public void onSubmitFB(Activity arg0) {			
				Map<String, String> remarkMap = new HashMap<String, String>();
				TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			    String deviceId = manager.getDeviceId();
				remarkMap.put("sid",deviceId);
				UMFeedbackService.setRemarkMap(remarkMap);
				
				if(isShowTips){
				Toast.makeText(MoreActivity.this,getString(R.string.thanks_for_feedback),
						 Toast.LENGTH_LONG).show();
				isShowTips = false;
				}
			}		
			@Override
			public void onResetFB(Activity arg0, Map<String, String> contactMap,
					Map<String, String> remarkMap) {			 
			}
		};
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					Intent it = new Intent(MoreActivity.this, ImageDetailActivity.class);
					startActivity(it);
					break;
				case 1:	
					UMFeedbackService.openUmengFeedbackSDK(MoreActivity.this);
					UMFeedbackService.setFeedBackListener(listener);					
					break;
				case 2:
					InputStream is = null;
					try {
						is = MoreActivity.this.getAssets().open("share.png");
					} catch (IOException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("image/*");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
					intent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.share_value));
					Uri uri = Uri.fromFile(getFile(is));
					intent.putExtra(Intent.EXTRA_STREAM,uri);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 			
					
					
					startActivity(Intent.createChooser(intent, getTitle()));					
					break;
				case 3:
					long cacheSize = getCacheSize()/(1024);
					String cache = null;
					if(cacheSize<10000){
						cache = cacheSize+"kb";
					}else{
						cache = cacheSize/1024+"M";
					}
					if(cacheSize!=0){
						new AlertDialog.Builder(MoreActivity.this).setMessage("本地缓存数据"+cache+",是否清空？").
						setPositiveButton(getResources().getString(R.string.clear_cache), new OnClickListener() {						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								clearCacheDir();  
								Toast.makeText(MoreActivity.this,R.string.clear_cache_success, Toast.LENGTH_SHORT).show();
							}
						}).setNegativeButton(getResources().getString(R.string.keep_cache), null).show();
					}else{
						Toast.makeText(MoreActivity.this,getResources().getString(R.string.no_cache),Toast.LENGTH_SHORT ).show();
					}
					break;
				case 4:
					Intent intentUri = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.sythealth.com/mactivity.html"));
					startActivity(intentUri);				
					break;
				case 5:
					addFragment(WebFragment.newInstance("file:///android_asset/aboutsyt.html"), WebFragment.TAG, android.R.id.content);
					break;
				case 6:
					addFragment(WebFragment.newInstance("file:///android_asset/terms.html"), WebFragment.TAG, android.R.id.content);
					break;
				}
			}
		});
	}
	class ListAdapter extends BaseAdapter{
		private String[] datas;
		private int [] icons;
		private ListAdapter(String[] datas,int [] icons){
			this.datas = datas;
			this.icons = icons;
		}
		@Override
		public int getCount() {
			return datas.length;
		}

		@Override
		public Object getItem(int arg0) {
			return datas[arg0];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView item_tv;
			ImageView icon_iv;
			LayoutInflater inflater = LayoutInflater.from(MoreActivity.this);
			convertView = inflater.inflate(R.layout.more_lv_item, null);
			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,100));
			item_tv = (TextView)convertView.findViewById(R.id.more_lv_item_tv);
			icon_iv = (ImageView)convertView.findViewById(R.id.more_lv_item_icon);
			item_tv.setText(datas[position]);
			icon_iv.setImageResource(icons[position]);
			return convertView;
		}		
	}	
	private File getFile(InputStream is){
		File file = new File("");
		OutputStream os;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[]buffer = new byte[1024];
			while ((bytesRead = is.read(buffer, 0, 1024)) != -1) { 
				os.write(buffer, 0, bytesRead); 
				} 
				os.close(); 
				is.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return file;
	}
}

