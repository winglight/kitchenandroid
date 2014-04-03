package com.syt.health.kitchen;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syt.health.kitchen.db.CourseModel;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.DateUtils;
import com.syt.health.kitchen.utils.DeliverData;
import com.syt.health.kitchen.utils.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ResetListActivity extends BaseActivity {
	private String date;
	private TextView date_tv;
	private List<String> dates = new ArrayList<String>();
	private Button up_btn;
	private Button next_btn;
	private Button show_menu;
	private ServiceImpl service;
	private Date d = new Date();
	private Meal lunchMeal= new Meal();
	private Meal dinnerMeal= new Meal();
	private ExpandableListAdapter adapter;
	private List<String> group = new ArrayList<String>();
	private LayoutInflater inflater;
	private Map<String, List<List<CourseFood>>> allMap = new HashMap<String, List<List<CourseFood>>>();//对应日期的所有菜
	private Map<String,List<CourseFood>> selectedMaps = new HashMap<String, List<CourseFood>>();//对应日期选中的菜
	private List<CourseFood> total_courseFoods = new ArrayList<CourseFood>();//所有选中的食材
	private final static String KEY1="中餐";
	private final static String KEY2="晚餐";
	private ViewPager viewPager;
	private List<View> mViews = new ArrayList<View>();
	private int currIndex=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetlist);   
		inflater = LayoutInflater.from(this);
		service =getService();
		date = DateUtils.defaultFormat(d); 
		for (int i = 0; i < 7; i++) {
			dates.add(DateUtils.defaultFormat(DateUtils.addDateDays(d, i)));
		}
		init();
		date_tv.setText(date);
	}
	private void init(){
		viewPager = (ViewPager)findViewById(R.id.activity_resetlist_viewpager);
		date_tv = (TextView)findViewById(R.id.activity_resetlist_date_tv);
		up_btn = (Button)findViewById(R.id.activity_resetlist_up_btn);
		show_menu = (Button)findViewById(R.id.activity_resetlist_showmenu_btn);
		show_menu.setOnClickListener(showMenu_btn_listener);
		up_btn.setOnClickListener(up_btn_listener);
		next_btn = (Button)findViewById(R.id.activity_restlist_next_btn);
		next_btn.setOnClickListener(next_btn_listener);
		addView();
		viewPager.setAdapter(new MyPagerAdapter(mViews));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyPagerChangeListener());
		viewPager.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//当返回true时 禁止随着手指滑动
				return true;
			}
		});
	}
	private void addView(){
		List<Menu> menus = service.getMenuIn7Days();
		for (int j = 0; j < 7; j++) {
			if(j<menus.size()){
					if(menus.get(j)!=null){
						View view = LayoutInflater.from(this).inflate(R.layout.resetlistactivity_expandablelistview, null);
						ExpandableListView expandableListView = (ExpandableListView)view.findViewById(R.id.activity_resetlist_food_expandableListView);
						expandableListView.setOnChildClickListener(expandableListView_listener);
						initializeData(menus.get(j));
						adapter = new ExpandableListAdapter(menus.get(j).getMenudate());
						expandableListView.setAdapter(adapter);
						mViews.add(view);
				}
			}else{
				TextView tv = new TextView(ResetListActivity.this);
				tv.setText("您还未设定当前日期的餐次");
				mViews.add(tv);
			}
		}	
	}
	//初始化数据
	private void initializeData(Menu menu){		
		if(menu!=null){
			lunchMeal = menu.getLunch();
			dinnerMeal = menu.getDinner();
			group.clear();
			List<List<CourseFood>> courseFood = new ArrayList<List<CourseFood>>();
			List<CourseFood> selectedCourseFood = new ArrayList<CourseFood>();
			if(lunchMeal!=null){
				group.add(KEY1);
				Map<String, List<CourseFood>> meal_map = new HashMap<String, List<CourseFood>>();
				meal_map.put(menu.getMenudate(), getChildItem(getCourses(lunchMeal)));
				List<CourseFood> lists = getChildItem(getCourses(lunchMeal));
				for (CourseFood courseFood2 : lists) {
					selectedCourseFood.add(courseFood2);
				}
				courseFood.add(lists);
			}else{ 
				Toast.makeText(ResetListActivity.this,ResetListActivity.this.getResources().getString(R.string.no_lunch),Toast.LENGTH_LONG).show();
			}
			if(dinnerMeal!=null){
				group.add(KEY2);
				List<CourseFood> lists = getChildItem(getCourses(dinnerMeal));
				for (CourseFood courseFood2 : lists) {
					selectedCourseFood.add(courseFood2);
				}
				courseFood.add(lists);
			}else{
				Toast.makeText(ResetListActivity.this,ResetListActivity.this.getResources().getString(R.string.no_dinner),Toast.LENGTH_LONG).show();
			}		
			   selectedMaps.put(menu.getMenudate(), selectedCourseFood);
		       allMap.put(menu.getMenudate(), courseFood);
		}
	}
	//显示菜单按钮点击事件
	private OnClickListener showMenu_btn_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {	
			total_courseFoods.clear();
			for (String date : dates) {
				if(selectedMaps.get(date)!=null){
					List<CourseFood> courseFoods = selectedMaps.get(date);
					for (CourseFood courseFood : courseFoods) {					
							total_courseFoods.add(courseFood);
					}
				}
			}
			service.saveFoodList(getTotalCourseFood(total_courseFoods));
			Intent intent = new Intent();
			intent.setClass(ResetListActivity.this, BuyingActivity.class);
			startActivity(intent);
		}
	};
	private List<CourseFood> getTotalCourseFood(List<CourseFood> courseFoods){
		Map<String, CourseFood> maps = new HashMap<String, CourseFood>();
		for (CourseFood courseFood : courseFoods) {
			CourseFood newCourseFood = maps.get(courseFood.getFood().getId());
			if(newCourseFood == null)
				maps.put(courseFood.getFood().getId(), courseFood);
			else{
				newCourseFood.setQuantity(newCourseFood.getQuantity()+courseFood.getQuantity());   
				maps.put(courseFood.getFood().getId(), newCourseFood);
			}
		}
		return new ArrayList<CourseFood>(maps.values());
	}
	private OnClickListener up_btn_listener = new OnClickListener(){		
		@Override
		public void onClick(View v) {
			String now_date = date_tv.getText().toString();
			if(now_date.equals(dates.get(0))){
				Toast.makeText(ResetListActivity.this, "无效日期", Toast.LENGTH_LONG).show();
			}else{
				date_tv.setText(dates.get(dates.indexOf(now_date)-1));
				date = date_tv.getText().toString();
				if(currIndex !=0){
					viewPager.setCurrentItem(currIndex-1);
				}
			}		
		}
	};
	private OnClickListener next_btn_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			String now_date = date_tv.getText().toString();
			if(now_date.equals(dates.get(6))){
				Toast.makeText(ResetListActivity.this, "最后一天", Toast.LENGTH_LONG).show();
			}else{
				date_tv.setText(dates.get(dates.indexOf(now_date)+1));
				date = date_tv.getText().toString();
				if(currIndex !=6){
					viewPager.setCurrentItem(currIndex+1);
				}
			}
		}  
	};
	private OnChildClickListener expandableListView_listener = new OnChildClickListener() {		
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			Holder holder = (Holder)v.getTag();
			CheckBox cb = holder.cb;
			if(Utils.listContains((CourseFood)holder.tv.getTag(), selectedMaps.get(date))){
				Utils.listRemove((CourseFood)holder.tv.getTag(), selectedMaps.get(date));
				cb.setChecked(false);
			}else{
				Utils.listAdd((CourseFood)holder.tv.getTag(),selectedMaps.get(date));
				cb.setChecked(true);
			}
			return false;
		}
	};
	class ExpandableListAdapter extends BaseExpandableListAdapter{
		private String date_key;

		public ExpandableListAdapter(String date_key) {
			this.date_key = date_key;

		}
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return allMap.get(date_key).get(groupPosition).get(childPosition);
		}
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			Holder holder;
			if(convertView == null){
				holder = new Holder();
				convertView = inflater.inflate(R.layout.resetactivity_list_item, null);
				holder.tv = (TextView)convertView.findViewById(R.id.resetactivity_list_item_tv);
				holder.cb = (CheckBox)convertView.findViewById(R.id.resetactivity_lsit_item_cb);
				convertView.setTag(holder);
			}else{
				holder = (Holder)convertView.getTag();
			}
			CourseFood courseFood = allMap.get(date_key).get(groupPosition).get(childPosition);
			holder.tv.setText(courseFood.getFood().getName());
			holder.tv.setTag(courseFood);
			boolean flag = !Utils.listContains(courseFood,selectedMaps.get(date_key));
			holder.cb.setChecked(flag);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return allMap.get(date_key).get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return group.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return group.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv = Utils.getGenericView(ResetListActivity.this);
			tv.setText(group.get(groupPosition));
			return tv;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
	 static class Holder{
		CheckBox cb;
		TextView tv;
	}
	 private List<Course> getCourses(Meal meal){
		 List<Course>courses = new ArrayList<Course>();
		 if(meal !=null){
			 MealCourse [] mealCourses = meal.getItems();
			 for (int i = 0; i < mealCourses.length; i++) {
					MealCourse mealCourse = mealCourses[i];
					Course course = mealCourse.getCourse();
					courses.add(course);
				}
			 return courses;
		 }
		 return null;
	 }
	 //获取全部的菜品集合
	 private List<CourseFood> getChildItem(List<Course> courses){
		 List<CourseFood> childItems = new ArrayList<CourseFood>();
			if(courses!=null&&courses.size()!=0){
				for (Course course : courses) {
					List<CourseFood> courseFoods = new ArrayList<CourseFood>();
					courseFoods = course.getItems();
					if(courseFoods!=null&&courseFoods.size()!=0){
						for (CourseFood courseFood : courseFoods) {		
							if(!childItems.contains(courseFood)){
								childItems.add(courseFood);				
							}									
						}
					}
				}
			 return childItems;
			}
			return null;
	 }
       class MyPagerAdapter extends PagerAdapter{
    	   private List<View> mViews;
    	   public MyPagerAdapter(List<View> views) {
    		   this.mViews = views;
		}
		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}	
		@Override
		public void destroyItem(View container, int position, Object object) {}
		@Override
		public Object instantiateItem(View container, int position) {
			try {
				((ViewPager) container).addView(mViews.get(position%mViews.size()), 0);  
			} catch (Exception e) {
			}     
			return mViews.get(position%mViews.size());
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 ==(arg1);
		}		 
	 }
       class MyPagerChangeListener implements OnPageChangeListener{
		@Override
		public void onPageScrollStateChanged(int arg0) {}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		@Override
		public void onPageSelected(int arg0) {
			currIndex = arg0%mViews.size();
		}  	   
       }
}
