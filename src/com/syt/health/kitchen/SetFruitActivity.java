package com.syt.health.kitchen;

import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.fragment.FoodInfoFragment;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseCondition;
import com.syt.health.kitchen.json.CourseCondition2;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.Utils;
import com.syt.health.kitchen.widget.ArrayWheelAdapter;
import com.syt.health.kitchen.widget.FruitWheelAdapter;
import com.syt.health.kitchen.widget.OnWheelChangedListener;
import com.syt.health.kitchen.widget.WheelView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SetFruitActivity extends BaseActivity {
	private WheelView fruit_wv;
	private WheelView count_wv;
	private CourseCondition condition = new CourseCondition();
	private String[] fruit_weight;
	private TextView info_tv;
	private FruitListAdapter adapter;
	private Button addFruit_btn;
	private String date;
	private Meal fruit_meal;
	private Button save_btn;
	private int numPeople;
	private TextView recommend_tv;
	private TextView total_tv;
	private TextView surplus_tv;
	private Menu menu;
	private MealCourse mealCourse;
	private int position;
	private List<MealCourse>mealCourses = new ArrayList<MealCourse>();
	private List<Course> current_courses = new ArrayList<Course>();
	private List<Course> all_courses = new ArrayList<Course>();
	private List<Course> selected_courses = new ArrayList<Course>();
	private boolean isAll = false;
	
	private List<String> healthcondition;
	private GenerateCondition generateCondition;
	private TextView badTxt;
	
	private ServiceImpl service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setfruit);
		date = getIntent().getStringExtra(Utils.DATE_KEY);
		mealCourse = (MealCourse)getIntent().getSerializableExtra(Utils.MEALCOURSE);
		
		service = getService();
		
		List<String> types = new ArrayList<String>();
		types.add(Utils.COURSE_CONDITION_FRUIT);		
		generateCondition = service.getCurrentMenu().getSmartParams();
		healthcondition = generateCondition.getHealthcondition();
		numPeople = generateCondition.getPeople();
		condition.setCoursetype(types);
		condition.setHealthcondition(healthcondition);
		condition.setPage(-1);
		
		menu = service.getCurrentMenu();
		fruit_meal = menu.getFruit();
		mealCourses = fruit_meal.getItems();
		
		init();
		getFruitList();
		
	}		
	private void init(){
		SharedPreferences sp =  getSharedPreferences("sp_first",0);
		LinearLayout help_layout = (LinearLayout)findViewById(R.id.activity_setfruit_help_layout);
		if(sp.getBoolean(Utils.SETFRUIT_FALG, true)){
			Utils.addImageView(this, help_layout, R.drawable.help_set_fruit, 0, Utils.SETFRUIT_FALG, sp);
		}
		surplus_tv = (TextView)findViewById(R.id.activity_setfruit_surplus_tv);
		total_tv = (TextView)findViewById(R.id.activity_setfruit_total_tv);
		refreshDate();
		Log.i("tag", surplus_tv.getText().toString());
		recommend_tv = (TextView)findViewById(R.id.activity_setfruit_recommend_tv);
		recommend_tv.setText(getResources().getString(R.string.recommend_fruit_cal)+
				new Double(menu.getFruitAdviceCal(numPeople)).intValue()+
				getResources().getString(R.string.caluli));
		badTxt = (TextView)findViewById(R.id.activity_setfruit_fruit_bad_txt);
		fruit_wv = (WheelView)findViewById(R.id.activity_setfruit_fruit_wheel_view);
//		fruit_wv.setVisibleItems(3);
		fruit_wv.addChangingListener(new OnWheelChangedListener() {			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				info_tv.setText(getFruitInfo(current_courses.get(fruit_wv.getCurrentItem()).getName(), fruit_weight[count_wv.getCurrentItem()],count_wv.getCurrentItem()));
				Course course = current_courses.get(newValue);
				if(!Utils.listContains(course,Utils.convertMealCourse(mealCourses))){
					addFruit_btn.setText(getResources().getString(R.string.add));
				}else{
					addFruit_btn.setText(getResources().getString(R.string.modify));
				}
				List<String> list = course.getBadDesc(generateCondition.getHealthcondition()); 
				if(list != null && list.size() > 0){
					badTxt.setVisibility(View.VISIBLE);
					badTxt.setText(course.getName() + ":不宜" + Utils.arrayIntoString(list));
				}else{
					badTxt.setVisibility(View.GONE);
				}
			}
		});
		count_wv = (WheelView)findViewById(R.id.activity_setfruit_count_wheel_view);
//		count_wv.setVisibleItems(3);
		count_wv.addChangingListener(new OnWheelChangedListener() {		
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				info_tv.setText(getFruitInfo(current_courses.get(fruit_wv.getCurrentItem()).getName(), fruit_weight[count_wv.getCurrentItem()],count_wv.getCurrentItem()));
			}
		});
		info_tv = (TextView)findViewById(R.id.activity_setfruit_fruit_info_tv);
		ListView fruit_lv = (ListView)findViewById(R.id.activity_setfruit_fruit_lv);
		adapter = new FruitListAdapter();
		fruit_lv.setAdapter(adapter);
		fruit_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position,
					long id) {
				List<Food> fList = Utils.convertFruitCourse(mealCourses);
			    Intent intent = new Intent();
				intent.setClass(SetFruitActivity.this, FoodInfoActivity.class);
				intent.putExtra("index", position);
				intent.putExtra(FoodInfoActivity.FOODS_KEY, (ArrayList<Food>)fList);
				startActivity(intent);
				
			}
		});
		addFruit_btn = (Button)findViewById(R.id.activity_setfruit_add_fruit_btn);
		addFruit_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				    Course course = current_courses.get(fruit_wv.getCurrentItem());
					if(!Utils.listContains(course,Utils.convertMealCourse(mealCourses))){
						addFruit(course);
					}else{
						for (int i = 0; i < mealCourses.size(); i++) {
							if(mealCourses.get(i).getCourse().equals(course)){
								modifyFruitCount(mealCourses.get(i));
							}
						}
					}
			}
		});
		
		final CheckedTextView healthBtn = (CheckedTextView) findViewById(R.id.fragment_fruit_add_advice_btn);
		final CheckedTextView allBtn = (CheckedTextView) findViewById(R.id.fragment_fruit_add_all_btn);
		healthBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isAll){
					isAll = false;
					getFruitList();
					healthBtn.setChecked(!isAll);
					allBtn.setChecked(isAll);
				}
			}
		});
		
		allBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isAll){
					isAll = true;
					getFruitList();
					healthBtn.setChecked(!isAll);
					allBtn.setChecked(isAll);
				}
			}
		});
		
		save_btn = (Button)findViewById(R.id.activity_setfruit_save_btn);
		save_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
	}
	private void getFruitList(){
		if(!isAll){
			condition.setHealthcondition(healthcondition);
			condition.setGoodseason(null);
			current_courses.clear();
			current_courses.addAll(selected_courses);
		}else{
			condition.setHealthcondition(null);
			condition.setGoodseason(Utils.FRUIT_FOUR_SEASON);
			current_courses.clear();
			current_courses.addAll(all_courses);
		}
		
		if(current_courses == null || current_courses.size() == 0){
		final ProgressDialog pd = ProgressDialog.show(this, "", "正在获取适宜水果列表...");
		pd.setCancelable(true);
		service.fetchCourseByParam(new TaskCallBack<CourseCondition, MessageModel<List<Course>>>() {			
			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				if(result.isFlag()){
					if(result.getData()!=null){
						if(!isAll){
							selected_courses = result.getData();
						}else{
							all_courses = result.getData();
						}
						current_courses.addAll(result.getData());
						
						initializeData();
					}
				}else{
					Toast.makeText(SetFruitActivity.this,result.getMessage(), Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
			
			@Override
			public CourseCondition getParameters() {
				return condition;
			}
		});
		}else{
			initializeData();
		}
	}
	private void initializeData(){
		if(current_courses.size() == 0) return;
		
		fruit_wv.setAdapter(new FruitWheelAdapter(current_courses));
		fruit_weight = getResources().getStringArray(R.array.fruit_weight);
		count_wv.setAdapter(new ArrayWheelAdapter<String>(fruit_weight));
		if(mealCourse!=null){
			position = current_courses.indexOf(mealCourse.getCourse());
			if(position!=-1){
				fruit_wv.setCurrentItem(position);
				int cal = (int)Math.rint(mealCourse.getQuantity()*100);
				if(cal<1000){
					count_wv.setCurrentItem(Utils.convertArray(fruit_weight).indexOf(cal+getResources().getString(R.string.unit_g)));
				}else{
					count_wv.setCurrentItem(Utils.convertArray(fruit_weight).indexOf(cal/1000+getResources().getString(R.string.unit_kg)));
				}			
				addFruit_btn.setText(getResources().getString(R.string.modify));
			}
		}
		int index = fruit_wv.getCurrentItem();
		Course course;
		if(index < current_courses.size()){
			course = current_courses.get(index);
		}else{
			course = current_courses.get(0);
			fruit_wv.setCurrentItem(0);
		}
		info_tv.setText(getFruitInfo(course.getName(), fruit_weight[count_wv.getCurrentItem()],count_wv.getCurrentItem()));
		
		List<String> list = course.getBadDesc(generateCondition.getHealthcondition()); 
		if(list != null && list.size() > 0){
			badTxt.setVisibility(View.VISIBLE);
			badTxt.setText(course.getName() + ":不宜" + Utils.arrayIntoString(list));
		}else{
			badTxt.setVisibility(View.GONE);
		}
	}
	private String getFruitInfo(String name,String count,double calories){ 
		if(count_wv.getCurrentItem()>9){
			calories = current_courses.get(fruit_wv.getCurrentItem()).getCalories()*(Double.parseDouble(Utils.removeLastStr(fruit_weight[count_wv.getCurrentItem()]))*10);
		}else{
			calories = current_courses.get(fruit_wv.getCurrentItem()).getCalories()*(count_wv.getCurrentItem()+1);		
		}
		String info = "已选 "+count+" "+new Double(calories).intValue()+getResources().getString(R.string.caluli);
		return info;
	}
	class FruitListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mealCourses.size();
		}
		@Override
		public Object getItem(int position) {
			return mealCourses.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder h;
			h = new Holder();
			convertView = Utils.getConvertView(mealCourses.size(), LayoutInflater.from(SetFruitActivity.this), position);
			h.condition_tv = (TextView)convertView.findViewById(R.id.condition_lv_item_name_tv);
			h.delete_btn = (TextView)convertView.findViewById(R.id.condition_lv_item_delete_btn);
			h.count_tv = (TextView)convertView.findViewById(R.id.condition_lv_item_count_tv);
			h.cals_tv = (TextView)convertView.findViewById(R.id.condition_lv_item_cals_tv);

			final MealCourse mealCourse = mealCourses.get(position);
			double count = mealCourse.getQuantity()*100;
			double cals = mealCourse.getQuantity()*mealCourse.getCourse().getCalories();
			h.condition_tv.setText(mealCourse.getCourse().getName());
			h.count_tv.setText(new Double(count).intValue()+getResources().getString(R.string.unit_g));
			h.cals_tv.setText(new Double(cals).intValue()+getResources().getString(R.string.caluli));
			h.delete_btn.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					deleteFruit(mealCourse);
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
	private void addFruit(Course course){
		final ProgressDialog pd = ProgressDialog.show(this, "", "正在添加水果...");
		pd.setCancelable(true);
		final MealCourse mealCourse = new MealCourse();
		mealCourse.setCourse(course);
		if(count_wv.getCurrentItem()>9){
			String str = Utils.removeLastStr(fruit_weight[count_wv.getCurrentItem()]);
			mealCourse.setQuantity(Double.parseDouble(str)*10);
		}else{
			mealCourse.setQuantity(count_wv.getCurrentItem()+1);
		}
		 service.addCourseToMeal(date, mealCourse, new TaskCallBack<Meal, MessageModel<Course>>() {						
				@Override
				public void postExecute(MessageModel<Course> result) {
					if(result.isFlag()){
//						Toast.makeText(SetFruitActivity.this,getResources().getString(R.string.add_success), Toast.LENGTH_SHORT).show();
						adapter.notifyDataSetChanged();
						addFruit_btn.setText(getResources().getString(R.string.modify));
						refreshDate();
						
						
					}else{
						Toast.makeText(SetFruitActivity.this,result.getMessage(), Toast.LENGTH_SHORT).show();
					}
					pd.dismiss();
				}
				
				@Override
				public Meal getParameters() {
					return fruit_meal;
				}
			});
	}
	private void deleteFruit(MealCourse mealCourse){
		final ProgressDialog pd = ProgressDialog.show(this, "", "正在删除水果...");
		pd.setCancelable(true);
		service.removeCourseFromMeal(date, mealCourse, new TaskCallBack<Meal, MessageModel<Meal>>() {						
			@Override
			public void postExecute(MessageModel<Meal> result) {
				if(result.isFlag()){
					mealCourses = result.getData().getItems();
					refreshDate();
					addFruit_btn.setText(getResources().getString(R.string.add));
					
					adapter.notifyDataSetChanged();
//					Toast.makeText(SetFruitActivity.this,getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(SetFruitActivity.this,result.getMessage(), Toast.LENGTH_SHORT).show();
				}
				pd.dismiss();
			}
			
			@Override
			public Meal getParameters() {
				return fruit_meal;
			}
		});
	}
	private void modifyFruitCount(MealCourse mealCourse){
		if(count_wv.getCurrentItem()>9){
			String str = Utils.removeLastStr(fruit_weight[count_wv.getCurrentItem()]);
			mealCourse.setQuantity(Double.parseDouble(str)*10);
		}else{
			mealCourse.setQuantity(count_wv.getCurrentItem()+1);
		}
		final ProgressDialog pd = ProgressDialog.show(this, "", "正在保存...");
		service.modifyCourseToMeal(date, mealCourse, new TaskCallBack<Meal, MessageModel<Meal>>() {
			
			@Override
			public void postExecute(MessageModel<Meal> result) {
				if(result.isFlag()){
					mealCourses = result.getData().getItems();
//					Toast.makeText(SetFruitActivity.this,getResources().getString(R.string.modify), Toast.LENGTH_SHORT).show();
					refreshDate();
					adapter.notifyDataSetChanged();
				}else{
					Log.i("tag", "result.isFlag()="+result.isFlag());
				}
				pd.dismiss();
			}		
			@Override
			public Meal getParameters() {
				return fruit_meal;
			}
		});
	}
	private void refreshDate(){
		double cals = Math.round(menu.getFruitAdviceCal(numPeople)-menu.getTotalFruitCal());
		if(cals<0){
			surplus_tv.setTextColor(Color.RED);
		}else{
			surplus_tv.setTextColor(Color.BLACK);
		}
		surplus_tv.setText("剩余可选热量:"+new Double(cals).intValue()+getResources().getString(R.string.caluli));
		Log.i("tag","2="+surplus_tv.getText().toString());
		total_tv.setText("已选水果热量:"+new Double(menu.getTotalFruitCal()).intValue()+getResources().getString(R.string.caluli));
	}
}
