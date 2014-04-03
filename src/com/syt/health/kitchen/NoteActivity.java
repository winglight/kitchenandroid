package com.syt.health.kitchen;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.syt.health.kitchen.R.drawable;
import com.syt.health.kitchen.animation.AnimatedImageView;
import com.syt.health.kitchen.animation.Animator;
import com.syt.health.kitchen.animation.PageCurlAnimator;
import com.syt.health.kitchen.fragment.BuyingListFragment;
import com.syt.health.kitchen.fragment.HealthBibleFragment;
import com.syt.health.kitchen.fragment.MealFragment;
import com.syt.health.kitchen.fragment.NoteFragment;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.utils.DateUtils;
import com.syt.health.kitchen.utils.Utils;
import com.syt.health.kitchen.widget.BadgeView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;

import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class NoteActivity extends BaseActivity {
	private TextView today_date;
	private Button breakfast_btn;
	private Button lunch_btn;
	private Button dinner_btn;
	private Button yiji_btn;
	private FrameLayout frameLayout;
	private LinearLayout shadowLL;
	private String currentDate;
	private Menu menu;
	private LinearLayout date_layout;
	private long exitTime = 0;

	public static final String HISTORY = "history";
	private final static String LOG_TAG = "NoteActivity";

	private Handler uiHandler;
	private AnimatedImageView dummyView;
	private ViewSwitcher viewSwitcher;
	private int step = 0;
	private boolean healthadviceFlag;

	private String currentHealthCondition;

	private boolean isSearchBadCondition = false;
	
	private boolean isHealthBible = false;

	private View currentTab; // current tab button

	private WeakReference<NoteFragment> cacheNoteFrg;
	private WeakReference<HealthBibleFragment> cacheBiblFrg;
	
	private SharedPreferences sp;
	private LinearLayout layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.uiHandler = new Handler();
		healthadviceFlag = getIntent().getBooleanExtra(Utils.HEALTHADVICE,
				false);
		currentDate = DateUtils.defaultFormat(new Date());
		sp =  getSharedPreferences("sp_first",0);
		init();
	}
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		isHealthBible = yiji_btn.equals(currentTab);
	}

	/***
	 * 初始化控件
	 */
	private void init() {	
		layout = (LinearLayout)findViewById(R.id.activity_main_help_layout);
		if(!healthadviceFlag){
			if(sp.getBoolean(Utils.NOTE_FLAG_01, true)){
				Utils.addImageView(this, layout, R.drawable.help_note_01,0, Utils.NOTE_FLAG_01, sp);
			}
		}

		frameLayout = (FrameLayout) findViewById(R.id.activity_main_framelayout);
		shadowLL = (LinearLayout) findViewById(R.id.activity_main_shadow_ll);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.mainContainer);
		dummyView = (AnimatedImageView) findViewById(R.id.dummyView);

		dinner_btn = (Button) findViewById(R.id.activity_main_dinner_btn);
		dinner_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				today_date.setTextColor(Color.GRAY);
				if (!currentTab.equals(v) && menu != null) {
					if(menu == null){
						Toast.makeText(NoteActivity.this,R.string.no_menu_tips,
								Toast.LENGTH_LONG).show();
						return;
					}
					Meal meal = menu.getDinner();

					if (meal != null) {
						clearFragment();
						if(sp.getBoolean(Utils.MEAL_FLAG, true)){
							Utils.addImageView(NoteActivity.this, layout, R.drawable.help_meal_dinner,0, Utils.MEAL_FLAG, sp);
						}
						MealFragment mFragment = MealFragment.newInstance(
								currentDate, Utils.DINNER);
						addFragment(mFragment, MealFragment.TAG,
								R.id.activity_main_left_linear);
					} else {
						Toast.makeText(NoteActivity.this,
								getResources().getString(R.string.prompt_02),
								Toast.LENGTH_LONG).show();
					}
					currentTab = v;
					showTabView(v);
				}

			}
		});

		lunch_btn = (Button) findViewById(R.id.activity_main_lunch_btn);
		lunch_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				today_date.setTextColor(Color.GRAY);
				if (!currentTab.equals(v) && menu != null) {
					if(menu == null){
						Toast.makeText(NoteActivity.this,R.string.no_menu_tips,
								Toast.LENGTH_LONG).show();
						return;
					}
					
					Meal meal = menu.getLunch();
					if (meal != null) {
						clearFragment();
						if(sp.getBoolean(Utils.MEAL_FLAG, true)){
							Utils.addImageView(NoteActivity.this, layout, R.drawable.help_meal_lunch,0, Utils.MEAL_FLAG, sp);
						}
						MealFragment mFragment = MealFragment.newInstance(
								currentDate, Utils.LUNCH);
						addFragment(mFragment, MealFragment.TAG,
								R.id.activity_main_left_linear);
					} else {
						Toast.makeText(NoteActivity.this,
								getResources().getString(R.string.prompt_02),
								Toast.LENGTH_LONG).show();
					}
					currentTab = v;
					showTabView(v);
				}

			}
		});
		lunch_btn.bringToFront();

		breakfast_btn = (Button) findViewById(R.id.activity_main_breakfast_btn);
		breakfast_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				today_date.setTextColor(Color.GRAY);
				if (!currentTab.equals(v) && menu != null) {
					if(menu == null){
						Toast.makeText(NoteActivity.this,R.string.no_menu_tips,
								Toast.LENGTH_LONG).show();
						return;
					}
					
					Meal meal = menu.getBreakfast();
					if (meal != null) {
						clearFragment();
						MealFragment mFragment = MealFragment.newInstance(
								currentDate, Utils.BREAKFAST);

						addFragment(mFragment, MealFragment.TAG,
								R.id.activity_main_left_linear);
					} else {
						Toast.makeText(NoteActivity.this,
								getResources().getString(R.string.prompt_02),
								Toast.LENGTH_LONG).show();
					}
					currentTab = v;
					showTabView(v);
				}

			}
		});
		breakfast_btn.bringToFront();
		date_layout = (LinearLayout) findViewById(R.id.activity_main_date_layout);
		today_date = (TextView) findViewById(R.id.activity_main_today_date_btn);
		currentTab = date_layout;
		date_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				today_date.setTextColor(Color.BLACK);
				if (!currentTab.equals(v)) {
//					if(menu == null){
//						Toast.makeText(NoteActivity.this,R.string.no_menu_tips,
//								Toast.LENGTH_LONG).show();
//						return;
//					}
					
					clearFragment();
					if (cacheNoteFrg == null || cacheNoteFrg.get() == null) {
						cacheNoteFrg = new WeakReference<NoteFragment>(
								NoteFragment.newInstance());
					}
					addFragment(cacheNoteFrg.get(), NoteFragment.TAG,
							R.id.activity_main_left_linear);
					currentTab = v;
					showTabView(v);
				}
			}
		});
		date_layout.bringToFront();
		today_date.setText(DateUtils.getMonthDay(getDate(), true));

		yiji_btn = (Button) findViewById(R.id.activity_main_yiji_btn);
		yiji_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				today_date.setTextColor(Color.GRAY);
				if (!currentTab.equals(v)) {
					clearFragment();
					if(sp.getBoolean(Utils.HEALTHCONDITION_FLAG, true)){
						Utils.addImageView(NoteActivity.this, layout, R.drawable.help_healcondition, 0, Utils.HEALTHCONDITION_FLAG, sp);
					}
					if (cacheBiblFrg == null || cacheBiblFrg.get() == null) {
						cacheBiblFrg = new WeakReference<HealthBibleFragment>(
								HealthBibleFragment.newInstance());
					}
					addFragment(cacheBiblFrg.get(),
							HealthBibleFragment.TAG, R.id.activity_main_left_linear);
					currentTab = v;
					showTabView(v);
				}
			}
		});
		if (healthadviceFlag) {
			if(sp.getBoolean(Utils.HEALTHCONDITION_FLAG, true)){
				Utils.addImageView(NoteActivity.this, layout, R.drawable.help_healcondition, 0, Utils.HEALTHCONDITION_FLAG, sp);
			}
			today_date.setTextColor(Color.GRAY);
			clearFragment();
			currentTab = yiji_btn;
			showTabView(yiji_btn);
			addFragment(HealthBibleFragment.newInstance(),
					HealthBibleFragment.TAG, R.id.activity_main_left_linear);

		} else {
			showTabView(currentTab);
			cacheNoteFrg = new WeakReference<NoteFragment>(
					NoteFragment.newInstance());
			addFragment(cacheNoteFrg.get(), NoteFragment.TAG,
					R.id.activity_main_left_linear);
		}
	}

	private void showTabView(View v) {
		try{
			isHealthBible = false;
		breakfast_btn
				.setBackgroundResource((breakfast_btn.equals(currentTab)) ? R.drawable.breakfast_btn_focus
						: R.drawable.breakfast_btn_normal);
		date_layout
				.setBackgroundResource((date_layout.equals(currentTab)) ? R.drawable.date_text_focus
						: R.drawable.date_text_normal);
		lunch_btn
				.setBackgroundResource((lunch_btn.equals(currentTab)) ? R.drawable.lunch_btn_focus
						: R.drawable.lunch_btn_normal);
		dinner_btn
				.setBackgroundResource((dinner_btn.equals(currentTab)) ? R.drawable.dinner_btn_focus
						: R.drawable.dinner_btn_normal);
		if(yiji_btn.equals(currentTab)){
		yiji_btn.setBackgroundResource(R.drawable.health_taboo_focus);
		isHealthBible = true;
		}else{
			yiji_btn.setBackgroundResource(R.drawable.health_taboo_normal);
		}

		shadowLL.bringToFront();
		v.bringToFront();
		frameLayout.invalidate();
		}catch(RuntimeException re){
    		Log.e(LOG_TAG, "RuntimeException");
    	}catch(OutOfMemoryError oe){
    		Log.e(LOG_TAG, "OutOfMemoryError");
    	}
	}

	/**
	 * 重写系统返回按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (step == 0) {
				if (System.currentTimeMillis() - exitTime > 2000) {

					Toast.makeText(NoteActivity.this,getResources().getString(R.string.click_again_exit),
							Toast.LENGTH_LONG).show();
					exitTime = System.currentTimeMillis();
					return true;
				} else {
					finish();
					System.exit(0);
				}
			} else if (step > 0) {
				step--;

			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void addFragment(android.support.v4.app.Fragment f, String tag,
			int id) {
//		synchronized (mPageLock) {
		
//		mPageLock.notifyAll();
//		}
		Bitmap before = getBookViewSnapshot();
		super.addFragment(f, tag, id);
//		Bitmap after = getBookViewSnapshot();
		
		doPageCurl(true, before, before);
//
		before = null;
//		after = null;
	}

	private void doPageCurl(boolean flipRight, Bitmap before, Bitmap after) {

		if (isAnimating()) {
			return;
		}

		this.viewSwitcher.setInAnimation(null);
		this.viewSwitcher.setOutAnimation(null);

		if (viewSwitcher.getCurrentView() != null
				&& viewSwitcher.getCurrentView() == this.dummyView) {
			viewSwitcher.showNext();
		}

		

		if (before == null)
			return;

		PageCurlAnimator animator = new PageCurlAnimator(flipRight);

		// Pagecurls should only take a few frames. When the screen gets
		// bigger, so do the frames.
		animator.SetCurlSpeed(viewSwitcher.getChildAt(0).getWidth() / 8);

		animator.setBackgroundColor(getResources().getColor(
				android.R.color.white));

		Log.d(LOG_TAG,
				"Before size: w=" + before.getWidth() + " h="
						+ before.getHeight());

		
		if (after == null)
			return;
		if (flipRight) {

			Log.d(LOG_TAG,
					"After size: w=" + after.getWidth() + " h="
							+ after.getHeight());
			animator.setBackgroundBitmap(after);
			animator.setForegroundBitmap(before);
		} else {
			Log.d(LOG_TAG,
					"After size: w=" + after.getWidth() + " h="
							+ after.getHeight());
			animator.setBackgroundBitmap(before);
			animator.setForegroundBitmap(after);
		}

		dummyView.setAnimator(animator);

		this.viewSwitcher.showNext();

		uiHandler.post(new PageCurlRunnable(animator));

		dummyView.invalidate();

	}

	private boolean isAnimating() {
		Animator anim = dummyView.getAnimator();
		return anim != null && !anim.isFinished();
	}

	private Bitmap getBookViewSnapshot() {

		try {
			Bitmap bitmap = Bitmap.createBitmap(viewSwitcher.getWidth(),
					viewSwitcher.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);

			viewSwitcher.getChildAt(0).layout(0, 0, viewSwitcher.getWidth(),
					viewSwitcher.getHeight());

			viewSwitcher.getChildAt(0).draw(canvas);

			return bitmap;
		} catch (OutOfMemoryError out) {
			viewSwitcher.setBackgroundColor(getResources().getColor(
					android.R.color.white));
		} catch (RuntimeException re) {
			viewSwitcher.setBackgroundColor(getResources().getColor(
					android.R.color.white));
		}

		return null;
	}

	private class PageCurlRunnable implements Runnable {

		private PageCurlAnimator animator;

		public PageCurlRunnable(PageCurlAnimator animator) {
			this.animator = animator;
		}

		@Override
		public void run() {

			if (this.animator.isFinished()) {

				if (viewSwitcher.getCurrentView() == dummyView) {
					viewSwitcher.showNext();
				}

				dummyView.setAnimator(null);

			} else {
				this.animator.advanceOneFrame();
				dummyView.invalidate();

				int delay = 1000 / this.animator.getAnimationSpeed();

				uiHandler.postDelayed(this, delay);
			}
		}
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void addStep() {
		this.step = step + 1;
	}

	public String getDate() {
		return currentDate;
	}

	public void setDate(String date) {
		this.currentDate = date;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public String getCurrentHealthCondition() {
		return currentHealthCondition;
	}

	public void setCurrentHealthCondition(String currentHealthCondition) {
		this.currentHealthCondition = currentHealthCondition;
	}

	public boolean isSearchBadCondition() {
		return isSearchBadCondition;
	}

	public void setSearchBadCondition(boolean isSearchBadCondition) {
		this.isSearchBadCondition = isSearchBadCondition;
	}

	public boolean isHealthBible() {
		return isHealthBible;
	}

	// the day before today is not allowed to change any menu or health
	// condition
	public boolean isReadOnly() {
		return DateUtils.beforeToday(currentDate);
	}

	public void showBreakfastBv(int count) {
		BadgeView bv = new BadgeView(this, breakfast_btn);
		bv.setText(count + "");
		bv.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		bv.setBadgeBackgroundColor(Color.parseColor("#A4C639"));
		TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		anim.setInterpolator(new BounceInterpolator());
		anim.setDuration(1000);
		bv.show(anim);
		// bv.hide();
	}
	
//	private void addImageView(final LinearLayout layout,int id1,final int id2,final String key){
//		try{
//		final LinearLayout layout = (LinearLayout)findViewById(R.id.activity_main_help_layout);
//		final ImageView iv = new ImageView(this);	
//		final WeakReference<Bitmap> bitmap = Utils.getBitmap(this,id1);
//		iv.setImageBitmap(bitmap.get());
//		iv.setTag(1);
//		iv.setScaleType(ScaleType.FIT_XY);
//		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		iv.setOnClickListener(new OnClickListener() {			
//			@Override
//			public void onClick(View v) {
//				int tag = (Integer) v.getTag();
//				if(id2 != 0){
//					if(tag==1){
//					
//						iv.setImageBitmap(Utils.getBitmap(NoteActivity.this,id2).get());
//						iv.setTag(2);
//				    }else{
//				    	sp.edit().putBoolean(key, false).commit();
//				    	layout.removeAllViews();
//				    }
//				}else{
//					sp.edit().putBoolean(key, false).commit();
//					
//					layout.removeAllViews();
//				}
//			}
//		});
//		layout.addView(iv);
//		}catch(RuntimeException re){
//    		Log.e(LOG_TAG, "RuntimeException");
//    	}catch(OutOfMemoryError oe){
//    		Log.e(LOG_TAG, "OutOfMemoryError");
//    	}
//	}
}
