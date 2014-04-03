package com.syt.health.kitchen;



import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GuideActivity extends Activity{
	private ViewPager vp;
	private ArrayList<View>pageViews = new ArrayList<View>();
	// 左右滑动时手指按下的X坐标  
    private float touchDownX; 
    // 左右滑动时手指松开的X坐标  
    private float touchUpX; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        init();
    }
	private void init() {
		vp = (ViewPager)findViewById(R.id.activity_guide_pages);
		for (int i = 0; i < 4; i++) {
			ImageView iv = new ImageView(this);
			if(i==0){
				iv.setImageBitmap(Utils.getSoftBitmap(GuideActivity.this, R.drawable.guide_page_one).get());
			}else if(i==1){
				iv.setImageBitmap(Utils.getSoftBitmap(GuideActivity.this, R.drawable.guide_page_two).get());
			}
			pageViews.add(iv);
		}
		
		
		vp.setAdapter(new GuidePageAdapter());
		vp.setOnPageChangeListener(new GuidePageChangeListener());
		vp.setCurrentItem(0);
	}
	  class GuidePageChangeListener implements OnPageChangeListener { 
	    	
	        @Override  
	        public void onPageScrollStateChanged(int arg0) {	        	
	        }  	  
	        @Override  
	        public void onPageScrolled(int arg0, float arg1, int arg2) {   
	        }
			@Override
			public void onPageSelected(int arg0) {		
				ImageView iv = (ImageView) pageViews.get(arg0);
				switch (arg0) {				
				case 1:					
					iv = (ImageView)pageViews.get(arg0+1);
					iv.setImageBitmap(Utils.getSoftBitmap(GuideActivity.this, R.drawable.guide_page_three).get());
					break;
				case 2:
					iv = (ImageView)pageViews.get(arg0+1);
					iv.setImageBitmap(Utils.getSoftBitmap(GuideActivity.this, R.drawable.guide_page_four).get());
					break;
				case 3:	
					iv.setOnTouchListener(new OnTouchListener() {					
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if(event.getAction() == MotionEvent.ACTION_DOWN){
								touchDownX = event.getX();
								return true;
							}
							else if(event.getAction() == MotionEvent.ACTION_CANCEL){
								touchUpX = event.getX();
								if(touchUpX-touchDownX<=0){  
									Intent intent = new Intent(GuideActivity.this,StartupActivity.class);
									startActivity(intent);
									finish();
									return true;  
								} 
							}
							return false;
						}            
					});
					break;
				}
				
			}  
	    }
	  /** 指引页面Adapter */
	    class GuidePageAdapter extends PagerAdapter {  
	    	  
	        @Override  
	        public int getCount() {  
	            return pageViews.size();  
	        }  
	  
	        @Override  
	        public boolean isViewFromObject(View arg0, Object arg1) {  
	            return arg0 == arg1;  
	        }  
	  
	        @Override  
	        public int getItemPosition(Object object) {  
	            return super.getItemPosition(object);  
	        }  
	  
	        @Override  
	        public void destroyItem(View arg0, int arg1, Object arg2) {  
	            ((ViewPager) arg0).removeView(pageViews.get(arg1));  
	        }  
	  
	        @Override  
	        public Object instantiateItem(View arg0, int arg1) {  
	        	try {
	        		 ((ViewPager) arg0).addView(pageViews.get(arg1), 0);  
				} catch (Exception e) {
				}
	            return pageViews.get(arg1);  
	        }  
	  
	        @Override  
	        public void restoreState(Parcelable arg0, ClassLoader arg1) {}  
	  
	        @Override  
	        public Parcelable saveState() {  
	            return null;  
	        }  
	  
	        @Override  
	        public void startUpdate(View arg0) {}  
	  
	        @Override  
	        public void finishUpdate(View arg0) {}  
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