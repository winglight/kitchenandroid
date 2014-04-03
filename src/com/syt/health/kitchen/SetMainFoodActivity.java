package com.syt.health.kitchen;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Text;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.Utils;
import com.syt.health.kitchen.widget.ArrayWheelAdapter;
import com.syt.health.kitchen.widget.OnWheelChangedListener;
import com.syt.health.kitchen.widget.WheelView;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SetMainFoodActivity extends BaseActivity {
	private WheelView quantity_wv;
	private WheelView point_wv;
	private String [] quantity;
	private String [] point;
	private TextView foodInfo_tv;
	private TextView name_tv;
	private TextView surplus_tv;
	private TextView total_tv;
	private TextView type_tv;
	private Button addFood_btn;
	private Meal meal;
	private String date;
	private int numPeople;
	private double count;
	private MealCourse mealCourse;
	private int initial_quantity=1;
	private int initial_point = 0;
	private List<MealCourse>mealCourses = new ArrayList<MealCourse>();
	private List<MealCourse>mainMealCourses = new ArrayList<MealCourse>();
	private ListAdapter adapter;
	private int type;
	private TextView recommend_tv;
	
	private ServiceImpl service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setmainfood);
		
		service = getService();
		
		numPeople = service.getCurrentMenu().getSmartParams().getPeople();
		quantity = getResources().getStringArray(R.array.quantity);
		point = getResources().getStringArray(R.array.point);
		type = getIntent().getIntExtra(Utils.MEAL_TYPE_KEY, 0);
		initData();
		init();
	}
	private void init(){
		ListView lv = (ListView)findViewById(R.id.activity_setmainfood_mainfood_lv);
		adapter = new ListAdapter();
		lv.setAdapter(adapter);
		type_tv = (TextView)findViewById(R.id.activity_setmainfood_type_tv);
		surplus_tv = (TextView)findViewById(R.id.activity_setmainfood_surplus_tv);
		recommend_tv = (TextView)findViewById(R.id.activity_setmainfood_recommend_tv);
		recommend_tv.setText(meal.getSetAdvicedCals(numPeople));
		total_tv = (TextView)findViewById(R.id.activity_setmainfood_total_tv);
		name_tv = (TextView)findViewById(R.id.activity_setmainfood_name_tv);
		name_tv.setText(mealCourse.getCourse().getName());
		foodInfo_tv = (TextView)findViewById(R.id.activity_setmainfood_mainfood_info_tv);
		quantity_wv = (WheelView)findViewById(R.id.activity_setmainfood_quantity_wheel_view);
		quantity_wv.setAdapter(new ArrayWheelAdapter<String>(quantity));
		quantity_wv.setCurrentItem(initial_quantity);
		quantity_wv.addChangingListener(new OnWheelChangedListener() {			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				foodInfo_tv.setText(getFoodInfo());
			}
		});
		point_wv = (WheelView)findViewById(R.id.activity_setmainfood_point_wheel_view);
		point_wv.setAdapter(new ArrayWheelAdapter<String>(point));
		point_wv.setCurrentItem(initial_point);
		point_wv.addChangingListener(new OnWheelChangedListener() {			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				foodInfo_tv.setText(getFoodInfo());
			}
		});
		foodInfo_tv.setText(getFoodInfo());
		addFood_btn = (Button)findViewById(R.id.activity_setmainfood_add_food_btn);
		addFood_btn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
					if(count!=0){
						if(!Utils.listContains(mealCourse, meal.getItems())){
							addCourse();
						}else{
							modifyCourse();
						}
					}else{
						Toast.makeText(SetMainFoodActivity.this,getString(R.string.set_count), Toast.LENGTH_LONG).show();
					}
			}
		});
		refreshData(mainMealCourses);
	}
	/*
	 * 获取主食的份上
	 */
	private String getFoodInfo(){
		count = Double.parseDouble(quantity[quantity_wv.getCurrentItem()])+Double.parseDouble(point[point_wv.getCurrentItem()]);		
		return "已选取"+count+"份"+",热量"+new Double(Math.floor(count*mealCourse.getCourse().getCalories())).intValue()+"千卡";
	}
	private void addCourse(){
		final ProgressDialog pd = ProgressDialog.show(this, "", "正在添加，请稍后。");
		
		mealCourse.setQuantity(count);
		Log.i("tag", "count="+count);
		service.addCourseToMeal(date, mealCourse, new TaskCallBack<Meal, MessageModel<Course>>() {			
			@Override
			public void postExecute(MessageModel<Course> result) {
				if(result.isFlag()){
//					Toast.makeText(SetMainFoodActivity.this,
//							getString(R.string.add_success), Toast.LENGTH_SHORT)
//							.show();
					initData();
//					Course course = result.getData();
//					if(course.getCoursecond().equals(Utils.COURSE_CONDITION_ZHUSHI)){
//						mainMealCourses.add(mealCourse);
//					}
					refreshData(mainMealCourses);
					adapter.notifyDataSetChanged();
					//finish();
				}else{
					Toast.makeText(SetMainFoodActivity.this,
							result.getMessage(), Toast.LENGTH_LONG)
							.show();
				//	finish();
				}
				pd.dismiss();
			}
			
			@Override
			public Meal getParameters() {
				return meal;
			}
		});
	}
	//修改菜品
	private void modifyCourse(){
		final ProgressDialog pd = ProgressDialog.show(this, "", "正在修改，请稍后。");
		
		mealCourse.setQuantity(count);
		service.modifyCourseToMeal(date, mealCourse, new TaskCallBack<Meal, MessageModel<Meal>>() {			
			@Override
			public void postExecute(MessageModel<Meal> result) {
				if(result.isFlag()){
//					Toast.makeText(SetMainFoodActivity.this,
//							"修改成功", Toast.LENGTH_SHORT)
//							.show();
					initData();
					refreshData(mainMealCourses);
					adapter.notifyDataSetChanged();
					//finish();
				}else{
					Toast.makeText(SetMainFoodActivity.this,
							result.getMessage(), Toast.LENGTH_LONG)
							.show();
				}
				pd.dismiss();
			}			
			@Override
			public Meal getParameters() {
				return meal;
			}
		});
	}
	class ListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mainMealCourses.size();
		}
		@Override
		public Object getItem(int position) {
			return mainMealCourses.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder h;
			h = new Holder();
			convertView = Utils.getConvertView(mainMealCourses.size(), LayoutInflater.from(SetMainFoodActivity.this), position);
			h.condition_tv = (TextView)convertView.findViewById(R.id.condition_lv_item_name_tv);
			h.delete_btn = (TextView)convertView.findViewById(R.id.condition_lv_item_delete_btn);
			h.count_tv = (TextView)convertView.findViewById(R.id.condition_lv_item_count_tv);
			h.cals_tv = (TextView)convertView.findViewById(R.id.condition_lv_item_cals_tv);
			final MealCourse mealCourse = mainMealCourses.get(position);
			double cals = mealCourse.getQuantity()*mealCourse.getCourse().getCalories();
			h.condition_tv.setText(mealCourse.getCourse().getName());
			h.count_tv.setText(mealCourse.getQuantity()+"份 ");
 			h.delete_btn.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					deleteCourse(mealCourse);
				}
			});
			return convertView;
		}		
	}
	class Holder{
		TextView condition_tv;
		TextView count_tv;
		TextView cals_tv;
		TextView delete_btn;
	}
	private void deleteCourse(final MealCourse mealCourse){
		final ProgressDialog pd = ProgressDialog.show(this, "", "正在删除...");
		
		service.removeCourseFromMeal(date, mealCourse, new TaskCallBack<Meal, MessageModel<Meal>>() {			
			@Override
			public void postExecute(MessageModel<Meal> result) {
				if(result.isFlag()){
					total_tv.setText(meal.getTotalCals(Utils.filterCourse(result.getData()), true));
					
					initData();
					refreshData(mainMealCourses);
					adapter.notifyDataSetChanged();
					if(mainMealCourses.size() == 0){
						type_tv.setText(null);
					}
					
					total_tv.setText(meal.getTotalCals(Utils.filterCourse(meal), true));
//					Toast.makeText(SetMainFoodActivity.this,getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(SetMainFoodActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}			  
			@Override
			public Meal getParameters() {
				return meal;
			}
		});		
	}
	private void initData(){
	
		meal = service.getCurrentMenu().getMealByType(type);
		mealCourses = meal.getItems();
		if(type==Utils.BREAKFAST){
			mainMealCourses = meal.getItems();
		}else{
			if(mealCourses.size()!=0){
				mainMealCourses.clear();
				for (int i = 0; i < mealCourses.size(); i++) {
					if(mealCourses.get(i).getCourse().getCoursecond().equals(Utils.COURSE_CONDITION_ZHUSHI)
							|| mealCourses.get(i).getCourse().getCoursecond().equals(Utils.COURSE_CONDITION_ZAODIAN)){
						mainMealCourses.add(mealCourses.get(i));
					}
				}
			}
		}
		date = getIntent().getStringExtra(Utils.DATE_KEY);
		mealCourse = (MealCourse)getIntent().getSerializableExtra(Utils.MEALCOURSE);
		
		if(mealCourse!=null){
			if(mealCourse.getQuantity()<1){
				initial_point = Utils.convertArray(point).indexOf(String.valueOf(mealCourse.getQuantity()));
			}else{
				String str = String.valueOf(mealCourse.getQuantity());
				initial_point = Utils.convertArray(point).indexOf("0"+str.subSequence(1, str.length()));
				initial_quantity = Utils.convertArray(quantity).indexOf(str.subSequence(0, str.length()-2));
			}
		}
	}
	private void refreshData(List<MealCourse> mealCourses){
		recommend_tv.setText(meal.getSetAdvicedCals(numPeople));
		if(Utils.listContains(mealCourse, mealCourses)){
			addFood_btn.setText(getString(R.string.modify));
		}else{
			addFood_btn.setText(getString(R.string.add));
		}
		int total = Integer.parseInt(Utils.removeLast(meal.getTotalCals(Utils.filterCourse(meal), true)));
		int recommend = Integer.parseInt(Utils.removeLast(recommend_tv.getText().toString()));
		int cals = recommend-total;
		if(cals<0){
			surplus_tv.setTextColor(Color.RED);
		}else{
			surplus_tv.setTextColor(Color.BLACK);
		}
		surplus_tv.setText("剩余可选热 量:"+cals+"千卡");
		total_tv.setText(meal.getTotalCals(Utils.filterCourse(meal), true));
		if(mainMealCourses.size() !=0){
	
			if(type==1){
				type_tv.setText("已选早点");
			}else{
				type_tv.setText("已选主食");
			}
		}else{
			type_tv.setText(null);

		}
	}
}
