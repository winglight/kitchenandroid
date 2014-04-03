package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.BuyingActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SettingsActivity;
import com.syt.health.kitchen.adapter.AutoCompleteAdapter;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceException;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.Calculate;
import com.syt.health.kitchen.utils.DateUtils;
import com.syt.health.kitchen.utils.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class TodayFragment extends Fragment {
	private ServiceImpl service;
	private TextView fruit_heat_tv;
	private TextView breakfast_heat_tv;
	private TextView lunch_heat_tv;
	private TextView dinner_heat_tv;
	private TextView fruit_numOfpeople_tv;
	private TextView breakfast_numOfpeople_tv;
	private TextView dinner_numOfpeople_tv;
	private TextView lunch_numOfpeople_tv;
	private Button breakfast_btn;
	private Button fruit_btn;
	private LayoutInflater mInflater;
	private int position;
	private ProgressDialog pd;
	private AutoCompleteAdapter adapter;
	private TextView heatTotal_tv;
	private Course mCourse;
	private LinearLayout food_linear;
	private AutoCompleteTextView autoTv;
	private List<MealCourse> fruit_mealCourses = new ArrayList<MealCourse>();
	private List<MealCourse> breakfast_mealCourses = new ArrayList<MealCourse>();
	private TextView people_tv;
	private Menu menu;
	private LinearLayout lunch_linear;
	private LinearLayout dinner_linear;
	private LinearLayout fruit_linear;
	private LinearLayout breakfast_linear;
	private LinearLayout add_lunch_linear;
	private LinearLayout add_dinner_linear;
	private Spinner date_sp;
	private ArrayAdapter<String> date_sp_adapter;
	private List<String> dates = new ArrayList<String>();
	private String date;
	private int type;
	private int isselect;
	private Button buyingList_btn;
	public final static String TAG= "TodayFragment";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    Date today = new Date();
	    date = DateUtils.defaultFormat(today);
	    service = ((BaseActivity)getActivity()).getService();
	}
	private void InitializationData(List<MealCourse> mealCourses,LinearLayout linearlayout,TextView heat_tv,TextView numOfpeople_tv){
		linearlayout.removeAllViews();
		for (int i = 0; i < mealCourses.size(); i++) {
			LinearLayout add_linearlayout = (LinearLayout)mInflater.inflate(R.layout.todayfragment_food_item, null);
			linearlayout.addView(add_linearlayout);
			TextView foodName_tv = (TextView)add_linearlayout.findViewById(R.id.todayfragment_item_foodname_tv);
			foodName_tv.setText(mealCourses.get(i).getCourse().getName());
		}
		int heat = Calculate.calculateTotalHeat(mealCourses);
		heat_tv.setText(heat+"千卡,");
		numOfpeople_tv.setText(Calculate.calcuatePerson(heat, 500)+"人");  
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_todaymenu, container,false);
		init(view);
		return view;
	}
	/*
	 * Initialization UI
	 */
	private void init(View view){
		date_sp = (Spinner)view.findViewById(R.id.fragment_todaymenu_date_spinner);
		Date d = new Date();
 		for (int i = 0; i <7; i++) {
 			dates.clear();
 			dates.add(DateUtils.defaultFormat(DateUtils.addDateDays(d,i)));
		}  
 		buyingList_btn = (Button)view.findViewById(R.id.fragment_todaymenu_buyingList_btn);
 		date_sp_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,dates);
 		date_sp_adapter.setDropDownViewResource(R.layout.spinner_item_text);
 		date_sp.setAdapter(date_sp_adapter);
		fruit_linear = (LinearLayout)view.findViewById(R.id.fragment_todaymenu_fruit_menu_linearlayout);      
		breakfast_linear = (LinearLayout)view.findViewById(R.id.fragment_todaymenu_breakfast_menu_linearlayout);
		lunch_linear = (LinearLayout)view.findViewById(R.id.fragment_todaymenu_lunch_menu_linearlayout);
		lunch_linear.setOnClickListener(click_listener);
		add_lunch_linear = (LinearLayout)view.findViewById(R.id.fragment_todaymenu_lunch_menu_add_linearlayout);
		dinner_linear = (LinearLayout)view.findViewById(R.id.fragment_todaymenu_dinner_menu_linearlayout);
		dinner_linear.setOnClickListener(click_listener);
		add_dinner_linear =(LinearLayout)view.findViewById(R.id.fragment_todaymenu_dinner_menu_add_linearlayout);
		mInflater = LayoutInflater.from(getActivity());  
		fruit_heat_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_fruit_heat_tv);
		breakfast_heat_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_breakfast_heat_tv);
		lunch_heat_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_lunch_heat_tv);
		dinner_heat_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_dinner_heat_tv);
		fruit_numOfpeople_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_fruit_numberOfpeople_tv);
		breakfast_numOfpeople_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_breakfast_numberOfpeople_tv);
		lunch_numOfpeople_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_lunch_numberOfpeople_tv);
		dinner_numOfpeople_tv = (TextView)view.findViewById(R.id.fragment_todaymenu_dinner_numberOfpeople_tv);
		fruit_btn = (Button)view.findViewById(R.id.fragment_todaymenu_fruit_set_btn);
		breakfast_btn = (Button)view.findViewById(R.id.fragment_todaymenu_breakfast_set_btn);
		fruit_btn.setOnClickListener(click_listener);
		breakfast_btn.setOnClickListener(click_listener);
		date_sp.setSelection(0, true);
		date_sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				date = date_sp_adapter.getItem(arg2);  
				fetchmenu(date);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		buyingList_btn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), BuyingActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onCreateOptionsMenu(android.view.Menu menu,
			MenuInflater inflater) {
		
		Date today = new Date();
		for(int i=0 ; i < 7 ; i++){
			Date dt = DateUtils.addDateDays(today, i);
			menu.add(DateUtils.defaultFormat(dt)).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}
	}
	

	/*
	 * Settings button listener
	 */
	private OnClickListener click_listener = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			if(fruit_btn == v){
				type = 0;   
				isselect = 1;
				showSetDialog();
			}else if(breakfast_btn == v){
				type = 1;
				isselect = 1;
				Log.i("tag", "menu="+menu);
				showSetDialog();
			}else if(lunch_linear == v){
				type = 2;
				//TODO:Temporarily 
				Meal meal = new Meal();
				if(menu.getLunch() !=null ){
					meal = menu.getLunch();
				}
				meal.setType(type);
				((BaseActivity) getActivity()).addFragment(MealTimesInfoFragment.newInstance(menu, meal), MealTimesInfoFragment.TAG);
			}else if(dinner_linear == v){  
				type=3;
				Meal meal = new Meal();
				if(menu.getDinner() !=null ){
					meal =menu.getDinner();
				}
				//TODO:Temporarily 
				meal.setType(type);
				((BaseActivity) getActivity()).addFragment(MealTimesInfoFragment.newInstance(menu,meal), MealTimesInfoFragment.TAG);
			}		
		}	
	};
	/**
	 * Create a dialog for setting food 
	 * @return
	 */
	private AlertDialog SetDialog(){
		return new AlertDialog.Builder(getActivity()).setTitle("").setPositiveButton("确认", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {			
					final Meal meal = new Meal();
					meal.setType(type);
					meal.setIsselect(isselect);
					if(type == 0){
						MealCourse[] c = fruit_mealCourses.toArray(new MealCourse[]{});
						meal.setItems(c);  	
					}else if(type == 1){
						MealCourse[] c =breakfast_mealCourses.toArray(new MealCourse[]{});
						meal.setItems(c);  	  
					}
						service.updateOrNewMeal(date, new TaskCallBack<Meal, MessageModel<Meal>>() {							
							@Override
							public void postExecute(MessageModel<Meal> result) {
								if(result.isFlag()){
									service.fetchMealsByDate(new TaskCallBack<String, MessageModel<Menu>>() {											
										@Override
										public void postExecute(MessageModel<Menu> result) {
											if(result.isFlag()){
												menu = result.getData();
												if(menu.getFruit()!=null){
													fruit_mealCourses =Utils.getMealCourses(menu.getFruit().getItems());													
													if(fruit_mealCourses.size() !=0){
														InitializationData(fruit_mealCourses, fruit_linear, fruit_heat_tv, fruit_numOfpeople_tv);
													}else{
														fruit_linear.removeAllViews();
														fruit_heat_tv.setText(null);
														fruit_numOfpeople_tv.setText(null);
													}
												}
												if(menu.getBreakfast()!=null){
													breakfast_mealCourses =Utils.getMealCourses(menu.getBreakfast().getItems());
												    if(breakfast_mealCourses.size()!=0){
												 
												    	InitializationData(breakfast_mealCourses, breakfast_linear, breakfast_heat_tv, breakfast_numOfpeople_tv);
												    }else{
												    	breakfast_linear.removeAllViews();
												    	breakfast_heat_tv.setText(null);
												    	breakfast_numOfpeople_tv.setText(null);
												    }
												}	
											}
										}										
										@Override
										public String getParameters() {
											return date;
										}
									});
								}
							}						
							@Override
							public Meal getParameters() {
								return meal;
							}
						});
				}		     
		}).setNegativeButton("取消", null).create();  
	}
	/**
	 * Initialization dialog data
	 * @param mealCourses
	 * @param linearlayout
	 * @param heat_tv
	 * @param numOfpeople_tv
	 */
	private void InitializationDialogData(final List<MealCourse> mealCourses,LinearLayout linearlayout,TextView heat_tv,TextView numOfpeople_tv){
		linearlayout.removeAllViews();
		for (int i = 0; i < mealCourses.size(); i++) {
			final MealCourse mealCourse = mealCourses.get(i);
			final LinearLayout add_linearlayout = (LinearLayout)mInflater.inflate(R.layout.dialog_food_item, null);
			linearlayout.addView(add_linearlayout);
			final TextView food_tv = (TextView)add_linearlayout.findViewById(R.id.food_item_foodname_tv);
			food_tv.setText(mealCourses.get(i).getCourse().getName()+":"+mealCourse.getQuantity()+"/"+mealCourse.getCourse().getUnit()+"*"+mealCourse.getCourse().getCalories());		
			Button add_btn = (Button)add_linearlayout.findViewById(R.id.food_item_add);
			Button minus_btn = (Button)add_linearlayout.findViewById(R.id.food_item_minus);				
			add_btn.setOnClickListener(new OnClickListener() {					
				@Override
				public void onClick(View v) {
					mealCourse.setQuantity(mealCourse.getQuantity()+1);
					food_tv.setText(mealCourse.getCourse().getName()+":"+mealCourse.getQuantity()+"*"+mealCourse.getCourse().getUnit()+"*"+Calculate.calculateHeat(mealCourse));
					int heat  = Calculate.calculateTotalHeat(mealCourses);
					heatTotal_tv.setText(heat+"千卡");
					people_tv.setText(Calculate.calcuatePerson(heat, 500)+"人");
				}   
			});
			minus_btn.setOnClickListener(new OnClickListener() {					
				@Override
				public void onClick(View v) {
					mealCourse.setQuantity(mealCourse.getQuantity()-1);
					food_tv.setText(mealCourse.getCourse().getName()+":"+mealCourse.getQuantity()+"*"+mealCourse.getCourse().getUnit()+"*"+Calculate.calculateHeat(mealCourse));
					int heat  = Calculate.calculateTotalHeat(mealCourses);
					heatTotal_tv.setText(heat+"千卡");
					people_tv.setText(Calculate.calcuatePerson(heat,500)+"人");
					if(mealCourse.getQuantity() == 0){
						mealCourses.remove(mealCourse);
						food_linear.removeView(add_linearlayout);
					}
				}
			});		
		}
		int heat =Calculate.calculateTotalHeat(mealCourses);
		heatTotal_tv.setText(heat+"千卡");
		people_tv.setText(Calculate.calcuatePerson(heat, 500)+"人");
	}
	/**
	 * Show dialog
	 */
	private void showSetDialog(){
		AlertDialog dialog = SetDialog();  
		final View view = mInflater.inflate(R.layout.fruitset_dialog, null);
		dialog.setView(view);
		autoTv = (AutoCompleteTextView)view.findViewById(R.id.fruitset_dialog_fruit_autocompletetv);	
		Button ok_btn = (Button)view.findViewById(R.id.fruitset_dialog_ok_btn);
		heatTotal_tv = (TextView)view.findViewById(R.id.fruitset_dialog_heattotal_tv);
		people_tv = (TextView)view.findViewById(R.id.fruitset_dialog_people_tv);
		food_linear = (LinearLayout)view.findViewById(R.id.fruit_dialog_fooditem_linearlayout);
		if(type ==0){
			if(fruit_mealCourses.size() !=0){
				food_linear.removeAllViews();
				InitializationDialogData(fruit_mealCourses, food_linear, heatTotal_tv, people_tv);
			}
		}else if(type == 1){
			if(breakfast_mealCourses.size() !=0){
				food_linear.removeAllViews();
				InitializationDialogData(breakfast_mealCourses, food_linear, heatTotal_tv, people_tv);
			}
		}
		autoTv.setThreshold(1);
		adapter = new AutoCompleteAdapter(getActivity(),R.layout.autotext_item);
		adapter.setService(service);
		autoTv.setAdapter(adapter);  
		autoTv.setOnItemClickListener(autocompletetext_listener);
		ok_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {  
				// TODO Auto-generated method stub
				if(autoTv.getText().toString().equals("")){
					Toast.makeText(getActivity(),"请输入菜名", 0).show();
				}else if(adapter.getCount() == 0){
					Toast.makeText(getActivity(),"没有菜品信息", 0).show();
				}else{
					getfoodInformation();
				}
			}
		});
		dialog.show();
	}
/**
 * Get food information by name
 */
	private void getfoodInformation(){
		pd = ProgressDialog.show(getActivity(), "", "正在获取菜品信息");
		 service.getCourseById(new TaskCallBack<String, MessageModel<Course>>() {					 
				@Override
				public void postExecute(MessageModel<Course> result) {
					if(result.isFlag()){								
						mCourse = result.getData();  						
					}else{
						Toast.makeText(getActivity(),"获取失败", 0).show();
					}
					 pd.dismiss();
				}					
				@Override   
				public String getParameters() {
					return adapter.getCourseId(position);
				}
			});
			MealCourse mealCourse = new MealCourse();
			mealCourse.setQuantity(1);
			if(mCourse!=null){
				Course course = new Course();
				course.setId(mCourse.getId());
				course.setName(mCourse.getName());
				course.setCalories(mCourse.getCalories()); 
				course.setUnit(mCourse.getUnit());
				mealCourse.setCourse(course); //每次重新new一个course 设置ID
				if(type == 0){
					fruit_mealCourses.add(mealCourse);
					InitializationDialogData(fruit_mealCourses, food_linear, heatTotal_tv,people_tv);		
				}else if(type == 1){
					breakfast_mealCourses.add(mealCourse);
					InitializationDialogData(breakfast_mealCourses, food_linear, heatTotal_tv,people_tv);	
				}
			}
	}
	/**
	 * Set autocomplete text 
	 */
	private OnItemClickListener autocompletetext_listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			position = arg2;
			heatTotal_tv.setText("");
		}
	};
	public static TodayFragment newInstance(){
		TodayFragment tf = new TodayFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("tf", 0);
		tf.setArguments(bundle);
		return tf;
	}
	private void fetchmenu(final String date){
		pd=ProgressDialog.show(getActivity(), "", "正在获取全天菜谱");
		fruit_linear.removeAllViews();
		breakfast_linear.removeAllViews();
		add_lunch_linear.removeAllViews();
		add_dinner_linear.removeAllViews();
		service.fetchMealsByDate(new TaskCallBack<String, MessageModel<Menu>>() {			
			@Override
			public void postExecute(MessageModel<Menu> result) {  
				if(result.isFlag()){
					menu = result.getData();			
					if(menu!=null){
						Meal fruit_meal = menu.getFruit();
						Meal breakfast_meal = menu.getBreakfast();
						if(fruit_meal!=null){
							fruit_mealCourses = Utils.getMealCourses(menu.getFruit().getItems());
							if(fruit_mealCourses.size() !=0){
								InitializationData(fruit_mealCourses, fruit_linear, fruit_heat_tv, fruit_numOfpeople_tv);
							}
						}if(breakfast_meal != null){
							breakfast_mealCourses = Utils.getMealCourses(menu.getBreakfast().getItems());
							if(breakfast_mealCourses.size()!=0){
								InitializationData(breakfast_mealCourses, breakfast_linear, breakfast_heat_tv, breakfast_numOfpeople_tv);
							}
						}if(menu.getLunch() !=null){  
							InitializationData(Utils.getMealCourses(menu.getLunch().getItems()), add_lunch_linear, lunch_heat_tv, lunch_numOfpeople_tv);
						}if(menu.getDinner()!=null){  
							InitializationData(Utils.getMealCourses(menu.getDinner().getItems()), add_dinner_linear, dinner_heat_tv, dinner_numOfpeople_tv);
						}
					}else{
						Toast.makeText(getActivity(), "出错 稍后再试试", 0).show();
					}
				}else{
					if(result.getErrorCode()==ServiceException.BUSINESS_CODE_NO_CONDITION){
						new AlertDialog.Builder(getActivity()).setTitle(getActivity().getResources().getString(R.string.dialog_title)).
						setMessage(getActivity().getResources().getString(R.string.prompt_02)).
						setPositiveButton(getActivity().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//跳到智能菜谱条件设置界面	
								Intent intent = new Intent(getActivity(), SettingsActivity.class);
								getActivity().startActivity(intent);
							}
						}).setNegativeButton(getActivity().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {						
							@Override
							public void onClick(DialogInterface dialog, int which) {   
								service.createMenuByDate(new TaskCallBack<String, MessageModel<Menu>>() {										
									@Override
									public void postExecute(MessageModel<Menu> result) {
										//Menu包括 id date
										if(result.isFlag()){
											menu = result.getData();
											if(menu!=null){
												if(menu.getLunch() ==null){
													lunch_heat_tv.setText(null);
													lunch_numOfpeople_tv.setText(null);
												}else if(menu.getDinner() == null){
													dinner_heat_tv.setText(null);
													dinner_numOfpeople_tv.setText(null);
												}
											}else{
												Toast.makeText(getActivity(), "出错 稍后再试试", 0).show();
											}											
										}else{
											Toast.makeText(getActivity(), "出错 稍后再试试", 0).show();
										}
										pd.dismiss();
									}									
									@Override
									public String getParameters() {
										return date;
									} 
								});
							}
						}).show();
					}
				}
				pd.dismiss();
			}
			@Override
			public String getParameters() {
				return date;
			}
		});
	}
	@Override
	public void onResume() {
		fetchmenu(date);
		super.onResume();
	}
}
