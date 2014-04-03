package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.fragment.CourseAddFragment.Holder;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseCondition;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.FoodCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.DateUtils;
import com.syt.health.kitchen.utils.Utils;
import com.syt.health.kitchen.widget.ActionItem;
import com.syt.health.kitchen.widget.ArrayWheelAdapter;
import com.syt.health.kitchen.widget.ClassifyWheelAdapter;
import com.syt.health.kitchen.widget.ConditionWheelAdapter;
import com.syt.health.kitchen.widget.NutrientsWheelAdapter;
import com.syt.health.kitchen.widget.OnWheelChangedListener;
import com.syt.health.kitchen.widget.QuickAction;
import com.syt.health.kitchen.widget.WheelView;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class HealthBibleFragment extends Fragment {
	
	public static final String TAG = "HealthBibleFragment";
	
	private AutoCompleteTextView search_auto;
	private TextView type_tv;
	private TextView cals_tv;
	private Button search_btn;
	private boolean typeFlag = true;//不同条件的不同类型
	private int model = 0;//0为菜品1为食材2为养身目标3为营养素4为热量目标
	private ListView data_lv;//数据列表
	private ServiceImpl service;   
	private LinearLayout setcondition_layout;//设置养生目标，营养素的布局
	private WheelView classify_wv;
	private WheelView condition_wv;
	private WheelView nutrient_wv;
	private List<NutrientModel>nutrientModels = null;
	private List<HealthCondClassifyModel>classifyModels = null;
	private List<Course>courses = new ArrayList<Course>();//菜品集合
	private List<Food> foods = new ArrayList<Food>();//食材集合
	private int page;//翻页
	private Button load_btn;//加载更多按钮
	private ProgressBar load_pb;//加载更多等待进度条
	private boolean loadFlag = true;//是否能够加载 true是false否
	private View footer_view;
	private View moreView;
	private FoodCondition foodCondition;
	private CourseCondition courseCondition;
	private String typeStr;//搜索种类
	private boolean conditionFlag = true;
	private Menu currentMenu;
	private String date;
	private Meal meal;
	private List<Menu> availableMenuList = new ArrayList<Menu>();
	private List<String> availableDateList = new ArrayList<String>();
	private CourseListAdapter courseAdapter;
	private List<Course> exist_course = new ArrayList<Course>();
	private FoodListAdapter foodAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		service = ((BaseActivity)getActivity()).getService();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try{
		View view = inflater.inflate(R.layout.fragment_health_bible, container,false);
		init(view);
		return view;
		}catch(RuntimeException re){
    		Log.e(TAG, re.getMessage());
    	}catch(OutOfMemoryError oe){
    		Log.e(TAG, oe.getMessage());
    	}
		return null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(search_auto != null){
			search_auto.setText("");
		}
//		model = 0;
		refreshLayout(model);
		
		availableMenuList = service.getMenuIn7Days();
		if (availableMenuList != null) {
			availableDateList.clear();
			for (Menu menu : availableMenuList) {
				availableDateList.add(DateUtils.getMonthDay(menu.getMenudate(), false));
			}
		}
		loadFlag = true;
	}
	
	private void init(View view){	
		nutrientModels = new ArrayList<NutrientModel>();
		nutrientModels.addAll(service.getNutrients());
		nutrientModels.remove(0);
		
		classifyModels = service.getAllHealthCondition();
		
		cals_tv = (TextView)view.findViewById(R.id.fragment_health_cals_tv);
		footer_view = LayoutInflater.from(getActivity()).inflate(
				R.layout.footer, null);
		moreView = LayoutInflater.from(getActivity()).inflate(
				R.layout.moredata, null);
		load_pb = (ProgressBar) moreView.findViewById(R.id.moredata_pb);
		load_btn = (Button) moreView.findViewById(R.id.moredata_load_btn);
		load_btn.setOnClickListener(loadBtn_listener);
		
		data_lv = (ListView)view.findViewById(R.id.fragment_health_lv);
		data_lv.setDivider(null);
		data_lv.setOnTouchListener(dataLv_touch);
		setcondition_layout = (LinearLayout)view.findViewById(R.id.fragment_health_setcondition_layout);
		
		search_auto = (AutoCompleteTextView)view.findViewById(R.id.fragment_health_search_auto);
		//search_auto.requestFocus();
		type_tv = (TextView)view.findViewById(R.id.fragment_health_type_btn);
		final LinearLayout condition_layout = (LinearLayout)view.findViewById(R.id.fragment_health_conditon_layout);
		final TextView condition_tv = (TextView)view.findViewById(R.id.fragment_health_condition_tv);
		final ListView condition_lv = (ListView)view.findViewById(R.id.fragment_health_condition_lv);
		condition_lv.setDivider(null);
		final String[] condition_arry =getResources().getStringArray(R.array.condition_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.textview, condition_arry);
		condition_lv.setAdapter(adapter);
		condition_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				condition_tv.setBackgroundResource(R.drawable.drop_downbtn_active);
				conditionFlag = true;
				loadFlag = true;
				model = arg2;
				courses.clear();
				foods.clear();
				refreshLayout(model);	
				condition_tv.setText(condition_arry[arg2]);
				condition_layout.setVisibility(View.GONE);
			}
		});
		condition_tv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(conditionFlag){
					condition_tv.setBackgroundResource(R.drawable.drop_downbtn_inactive);
					conditionFlag = false;
				
					condition_layout.setVisibility(View.VISIBLE);
					condition_layout.bringToFront();
				}else{
					condition_tv.setBackgroundResource(R.drawable.drop_downbtn_state);
					conditionFlag = true;
					condition_layout.setVisibility(View.GONE);
				}
			}
		});

		search_btn = (Button)view.findViewById(R.id.fragment_health_search_btn);
		search_btn.setOnClickListener(searchBtn_listener);
	}
	public static HealthBibleFragment newInstance(){
		HealthBibleFragment fragment = new HealthBibleFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}
	private OnClickListener loadBtn_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			if(loadFlag){
				load_btn.setVisibility(View.GONE);
				load_pb.setVisibility(View.VISIBLE);
				page++;
			switch (model) {
			case 0:
				searchCourse();
				break;
			case 1:
				searchFood();
				break;
			case 2:
				searchFoodByParam();
				break;
			case 3:
				searchFoodByParam();
				break;
			case 4:
				if(typeStr.equals("食材")){
					searchFoodByParam();
				}else{
					searchCourseByParam();
				}
				break;
			}
		}else{
			Toast.makeText(getActivity(),getString(R.string.all_conditions_data), Toast.LENGTH_LONG).show();
		}
	 }
	};
	//类型按钮监听
	private OnClickListener typeTv_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			courses.clear();
			foods.clear();
			loadFlag = true;
			
			if(foodAdapter!=null){
				foodAdapter.notifyDataSetChanged();
			}if(courseAdapter!=null){
				courseAdapter.notifyDataSetChanged();
			}
			data_lv.removeFooterView(moreView);
			switch (model) {
			case 2:	
				if(typeFlag){
					type_tv.setBackgroundResource(R.drawable.incompatible_typebtn_state);
					typeStr = getString(R.string.bads);
					typeFlag = false;
					((NoteActivity)getActivity()).setSearchBadCondition(true);
				}else{
					type_tv.setBackgroundResource(R.drawable.effective_typebtn_state);
					typeStr =getString(R.string.goods);
					typeFlag = true;
					((NoteActivity)getActivity()).setSearchBadCondition(false);
				}
				break;

			case 3:
				if(typeFlag){
					type_tv.setBackgroundResource(R.drawable.low_typebtn_state);
					typeStr = getString(R.string.low);
					typeFlag = false;
				}else{
					type_tv.setBackgroundResource(R.drawable.high_typebtn_state);
					typeStr = getString(R.string.high);
					typeFlag = true;
				}
				break;
			case 4:
				if(typeFlag){
					type_tv.setBackgroundResource(R.drawable.food_typebtn_state);
					typeStr = getString(R.string.food);
					typeFlag = false;
					cals_tv.setText("千卡/100克");
				}else{
					type_tv.setBackgroundResource(R.drawable.course_typebtn_state);
					typeStr = getString(R.string.course);
					cals_tv.setText(getString(R.string.caluli));
					typeFlag = true;
				}
				break;
			}
		}
	};
	//搜索按钮监听
	private OnClickListener searchBtn_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			if(!search_auto.getText().toString().equals("")){
				//隐藏键盘
				try{
				((InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE)).
				hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}catch(RuntimeException re){
	        		Log.e(TAG, "RuntimeException:" + re.getMessage());
	        	}
				page = 1;
				courses.clear();
				foods.clear();
				switch(model) {
				case 0:	
					data_lv.setPadding(0, 0, 5, 40);
					((NoteActivity)getActivity()).setCurrentHealthCondition(null);
					searchCourse();
					break;					
				case 1:
					data_lv.setPadding(0, 0, 5, 40);
					((NoteActivity)getActivity()).setCurrentHealthCondition(null);
					searchFood();
					break;
				case 2:
					((NoteActivity)getActivity()).setCurrentHealthCondition(search_auto.getText().toString());
					setAutoEnabled();
					foodCondition = FoodCondition.newByHealthInstance();
					foodCondition.setHealthcondition(search_auto.getText().toString());
					foodCondition.setFilter(typeStr);
					searchFoodByParam();
					break;
				case 3:
					((NoteActivity)getActivity()).setCurrentHealthCondition(null);
					setAutoEnabled();
					foodCondition = FoodCondition.newByNutrientsInstance();
					foodCondition.setNutrients(search_auto.getText().toString());
					foodCondition.setFilter(typeStr);
					searchFoodByParam();
					break;
				case 4:
					data_lv.setPadding(0, 0, 5, 40);
					((NoteActivity)getActivity()).setCurrentHealthCondition(null);
					int cals = Integer.parseInt(search_auto.getText().toString().trim());
					if(typeStr.equals(getString(R.string.food))){
						foodCondition = FoodCondition.newByCalsInstance();
						foodCondition.setCals(cals);
						searchFoodByParam();
					}else if(typeStr.equals(getString(R.string.course))){
						courseCondition = CourseCondition.newByCalsInstance();
						courseCondition.setCals(cals);
						searchCourseByParam();
					}
					break;
				}
			}else{
				Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.keywords), Toast.LENGTH_LONG).show();
			}
		}
	};
	//根据不同的条件显示不同的布局
	private void refreshLayout(int model){
		setcondition_layout.removeAllViews();
		search_auto.setText(null);
		data_lv.removeFooterView(moreView);
		((NoteActivity)getActivity()).setSearchBadCondition(false);
		typeFlag = true;
		switch (model) {
		case 0:
			cals_tv.setVisibility(View.GONE);
			setAutoAttribute();
			search_auto.setHint(R.string.search_course_tips);
			type_tv.setBackgroundResource(R.drawable.logo_typebtn);
			break;
		case 1:
			cals_tv.setVisibility(View.GONE);
			setAutoAttribute();
			search_auto.setHint(getString(R.string.auto_food));
			type_tv.setBackgroundResource(R.drawable.logo_typebtn);
			setcondition_layout.removeAllViews();
			break;
		case 2:
			cals_tv.setVisibility(View.GONE);
			typeStr = getString(R.string.goods);
			search_auto.setEnabled(false);
			search_auto.setInputType(InputType.TYPE_NULL);
			type_tv.setBackgroundResource(R.drawable.effective_typebtn_state);
			type_tv.setOnClickListener(typeTv_listener);
			setcondition_layout.addView(showSetCondition());
			setcondition_layout.setVisibility(View.VISIBLE);
			search_auto.setText(classifyModels.get(classify_wv.getCurrentItem()).
					getSubList().get(condition_wv.getCurrentItem()).getName());
			break;
		case 3:
			cals_tv.setVisibility(View.GONE);
			typeStr = getString(R.string.high);
			search_auto.setEnabled(false);
			search_auto.setInputType(InputType.TYPE_NULL);
			type_tv.setBackgroundResource(R.drawable.high_typebtn_state);
			type_tv.setOnClickListener(typeTv_listener);
			setcondition_layout.addView(showNutrient());
			setcondition_layout.setVisibility(View.VISIBLE);
			search_auto.setText(nutrientModels.get(nutrient_wv.getCurrentItem()).getName());
			break;
		case 4:
			cals_tv.setVisibility(View.VISIBLE);
			cals_tv.setText(getString(R.string.caluli));
			typeStr = getString(R.string.course);
			setAutoAttribute();
			search_auto.setInputType(InputType.TYPE_CLASS_NUMBER);
			search_auto.setHint("");
			type_tv.setBackgroundResource(R.drawable.course_typebtn_state);
			type_tv.setOnClickListener(typeTv_listener);
			break;
		}
	}
	//初始化设置营养素界面
	private View showNutrient(){
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.set_nutrient, null);
		nutrient_wv = (WheelView)view.findViewById(R.id.set_nutruen_wheel_view);
		nutrient_wv.setAdapter(new NutrientsWheelAdapter(nutrientModels));
		nutrient_wv.addChangingListener(new OnWheelChangedListener() {			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				search_auto.setText(nutrientModels.get(nutrient_wv.getCurrentItem()).getName());				
			}
		});
		return view;
	}
	//初始化设置养生条件界面
	private View showSetCondition(){
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.set_health_condition, null);
		classify_wv = (WheelView)view.findViewById(R.id.set_health_condition_classify_wheel_view);
		condition_wv = (WheelView)view.findViewById(R.id.set_health_condition_condition_wheel_view);
		condition_wv.setAdapter(new ConditionWheelAdapter(classifyModels.get(0).getSubList()));
		classify_wv.setAdapter(new ClassifyWheelAdapter(classifyModels));
		classify_wv.addChangingListener(new OnWheelChangedListener() {			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				condition_wv.setAdapter(new ConditionWheelAdapter(classifyModels.get(newValue).getSubList()));
				condition_wv.setCurrentItem(0);
				search_auto.setText(classifyModels.get(classify_wv.getCurrentItem()).
						getSubList().get(condition_wv.getCurrentItem()).getName());
			}
		});
		condition_wv.addChangingListener(new OnWheelChangedListener() {			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				search_auto.setText(classifyModels.get(classify_wv.getCurrentItem()).
						getSubList().get(condition_wv.getCurrentItem()).getName());
			}
		});
		classify_wv.setCurrentItem(0);
		return view;
	}
	//搜索框点击事件
	private OnClickListener search_click = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			setcondition_layout.setVisibility(View.VISIBLE);
			search_auto.setInputType(InputType.TYPE_NULL);
		}
	};
	//搜索框焦点事件
	private OnFocusChangeListener search_focus = new OnFocusChangeListener() {		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			setcondition_layout.setVisibility(View.VISIBLE);
			search_auto.setInputType(InputType.TYPE_NULL);	
			
		}
	};
	//设置搜索框属性
	private void setAutoAttribute(){
		search_auto.setOnClickListener(null);
		search_auto.setOnFocusChangeListener(null);
		search_auto.setInputType(InputType.TYPE_CLASS_TEXT);
		search_auto.setEnabled(true);
		setcondition_layout.setVisibility(View.GONE);
	}

	//搜索框是否可用
	private void setAutoEnabled(){
		setcondition_layout.setVisibility(View.GONE);
		search_auto.setEnabled(true);
		search_auto.setOnClickListener(search_click);
		search_auto.setOnFocusChangeListener(search_focus);
		data_lv.setPadding(0, 0, 5, 40);
	}

	//菜品列表的adapter
	class CourseListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return courses.size();
		}

		@Override
		public Object getItem(int position) {
			return courses.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			CourseHolder h;
			if (convertView == null) {
				h = new CourseHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.add__course_lv_item, null);
				h.course_iv = (ImageView) convertView
						.findViewById(R.id.add_course_lv_course_photo_iv);
				h.courseName_tv = (TextView) convertView
						.findViewById(R.id.add_course_lv_course_name_tv);
				h.good_iv = (ImageView) convertView
						.findViewById(R.id.add_course_lv_good_iv);
				h.bad_iv = (ImageView) convertView
						.findViewById(R.id.add_course_lv_bad_iv);
				h.courseInfo_tv = (TextView) convertView
						.findViewById(R.id.add_course_lv_course_info_tv);
				h.add_btn = (Button) convertView
						.findViewById(R.id.add_course_lv_add_iv);
				h.middle_view = (View) convertView
						.findViewById(R.id.add_course_lv_middle_view);
				h.header_layout = (LinearLayout)convertView.findViewById(R.id.add_course_lv_header_layout);
				convertView.setTag(h);
			} else {
				h = (CourseHolder) convertView.getTag();
			}
			if(position == 0){
				h.header_layout.setVisibility(View.VISIBLE);
			}else{  
				h.header_layout.setVisibility(View.GONE);
			}
			final Course course = courses.get(position);
			// 当courses的size小于9的时候 添加一个footer
			if (courses.size() <= 9) {
				if (position == courses.size() - 1) {
					h.middle_view.setVisibility(View.GONE);
				} else {
					h.middle_view.setVisibility(View.VISIBLE);
				}
			}
			final List<String> goodDesc =  course.getEffectivity();
			if(goodDesc!=null){
				h.good_iv.setImageResource(R.drawable.good_button_state);
				h.good_iv.setOnClickListener(new OnClickListener() {				
					@Override    
					public void onClick(View v) {
						QuickAction mAction = new QuickAction(getActivity());
						ActionItem item = new ActionItem(0,Utils.arrayIntoString(goodDesc));
						mAction.addActionItem(item);
						mAction.show(v);
					}
				});
			}else{
				h.good_iv.setImageResource(R.drawable.good_no_bg);
				h.good_iv.setOnClickListener(null);
			}
			final List<String> badDesc = course.getIncompatible();
			if(badDesc!=null){
				h.bad_iv.setImageResource(R.drawable.bad_button_state);
				h.bad_iv.setOnClickListener(new OnClickListener() {				
					@Override    
					public void onClick(View v) {
						QuickAction mAction = new QuickAction(getActivity());
						ActionItem item = new ActionItem(0,Utils.arrayIntoString(badDesc));
						mAction.addActionItem(item);
						mAction.show(v);
					}
				});
			}else{
				h.bad_iv.setImageResource(R.drawable.bad_no_bg);
				h.bad_iv.setOnClickListener(null);
			}
			((BaseActivity) getActivity()).getmImageFetcher().loadImage(
					course.getListPicUrl(), h.course_iv);
			h.courseName_tv.setText(course.getName());
			h.courseInfo_tv.setText(course.getCoursecond() + "   "
					+ course.getCalories() +getString(R.string.caluli));
			if(Utils.COURSE_CONDITION_ZZF.contains(course.getCoursecond())){
				h.add_btn.setVisibility(View.GONE);
			}else{
				h.add_btn.setVisibility(View.VISIBLE);
			if (!Utils.listContains(course, exist_course)) {
				h.add_btn
						.setBackgroundResource(R.drawable.add_lv_course_button_state);
				h.add_btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
							// show selector of date and meal type
							if (availableDateList.size() > 0) {
								
								showAddCourse((Button) v, position);
							} else {
								// no menu data can be added
								Toast.makeText(
										getActivity(),
										getResources().getString(
												R.string.no_menu_to_add_course,
												course.getName()),
										Toast.LENGTH_LONG).show();
								
							}

					}
				});
			} else {
				h.add_btn.setBackgroundResource(R.drawable.added_course_bg);
				h.add_btn.setOnClickListener(added_listener);
			}
		}
			return convertView;
		}		
	}
	class CourseHolder {
		LinearLayout header_layout;
		ImageView course_iv;
		TextView courseName_tv;
		ImageView good_iv;
		ImageView bad_iv;
		Button add_btn;
		TextView courseInfo_tv;
		View middle_view;
	}
	//搜索菜品数据
	private void searchCourse(){
		final ProgressDialog pd = ProgressDialog.show(getActivity(), null,getString(R.string.search_course));
		service.queryCourseByLikeWords(page, false, new TaskCallBack<String, MessageModel<List<Course>>>() {			
			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				List<Course> search_courses = new ArrayList<Course>();
				if(result.isFlag()){
					search_courses = result.getData();
					data_lv.removeFooterView(moreView);
					data_lv.removeFooterView(footer_view);
					if(page == 1){
						if(search_courses!=null){
							courses.addAll(search_courses);
						}else{
							Toast.makeText(getActivity(),getString(R.string.no_conditions_data), Toast.LENGTH_LONG).show();
						}
					}else{
						if(search_courses==null){
							Toast.makeText(getActivity(),getString(R.string.all_conditions_data), Toast.LENGTH_LONG).show();
							loadFlag = false;
						}else{
							courses.addAll(search_courses);
						}
					}
					if(courses.size()>9){
						data_lv.addFooterView(moreView);
					}else{
						if(courses.size()!=0){
							data_lv.addFooterView(footer_view);
						}
					}
					courseAdapter = new CourseListAdapter();
					data_lv.setAdapter(courseAdapter);
					/**
					 * 加载更多
					 * 如果add_course不为空，设置listview选中项为菜品的数量减去加载到的菜品数量
					 * 如果为空则设置Listview默认选中项为菜品的总数量
					 */
					if(search_courses!=null){
						data_lv.setSelection(courses.size()-search_courses.size());
					}else{
						data_lv.setSelection(courses.size());
					}
					data_lv.setOnItemClickListener(courseLv_listener);  				
				}else{
					Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
				load_btn.setVisibility(View.VISIBLE);
				load_pb.setVisibility(View.GONE);
			}
			
			@Override
			public String getParameters() {
				return search_auto.getText().toString();
			}
		});
	}	
	class FoodListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return foods.size();
		}
		@Override
		public Object getItem(int position) {
			return foods.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FoodHolder h;
			if(convertView == null){
				h = new FoodHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.food_lv_item, null);
				h.header_layout = (LinearLayout)convertView.findViewById(R.id.food_lv_header_layout);
				h.food_iv = (ImageView)convertView.findViewById(R.id.food_lv_photo_iv);
				h.name_tv = (TextView)convertView.findViewById(R.id.food_lv_name_tv);
				h.info_tv = (TextView)convertView.findViewById(R.id.food_lv_info_tv);
				h.middle_view = (View)convertView.findViewById(R.id.food_lv_middle_view);
				convertView.setTag(h);
			}else{
				h = (FoodHolder)convertView.getTag();
			}
			if(position == 0){
				h.header_layout.setVisibility(View.VISIBLE);
			}else{
				h.header_layout.setVisibility(View.GONE);
			}
			
			Food food = foods.get(position);
			// 当courses的size小于9的时候 添加一个footer
						if (foods.size() <= 9) {
							if (position == foods.size() - 1) {
								h.middle_view.setVisibility(View.GONE);
							} else {
								h.middle_view.setVisibility(View.VISIBLE);
							}
						}
			//加载食材图片
			((BaseActivity) getActivity()).getmImageFetcher().loadImage(
					food.getListPicUrl(), h.food_iv);
			h.name_tv.setText(food.getName());
			if(model == 3){
				NutrientModel nutrientModel = food.getNutrient();
				h.info_tv.setText(nutrientModel.getName()+"含量:"+new Double(Math.ceil(nutrientModel.getContent()*100)).intValue()+nutrientModel.getUnit());
			}else{
				h.info_tv.setText("标准热量:"+new Double(Math.ceil(food.getCalories()*100)).intValue()+"千卡");
			}
			return convertView;
		}		
	}
	class FoodHolder{
		LinearLayout header_layout;
		ImageView food_iv;
		TextView name_tv;
		TextView info_tv;
		View middle_view;
	}
	//食材查找
	private void searchFood(){
		final ProgressDialog pd = ProgressDialog.show(getActivity(), null,getString(R.string.search_course));
		service.queryFoodByLikeWords(page, new TaskCallBack<String, MessageModel<List<Food>>>() {			
			@Override
			public void postExecute(MessageModel<List<Food>> result) {
				List<Food>search_foods = new ArrayList<Food>();
				if(result.isFlag()){
					search_foods = result.getData();
					data_lv.removeFooterView(moreView);
					data_lv.removeFooterView(footer_view);
					if(page == 1){
						if(search_foods!=null){
							foods.addAll(search_foods);
						}else{
							Toast.makeText(getActivity(),getString(R.string.no_conditions_data), Toast.LENGTH_LONG).show();
						}
					}else{
						if(search_foods==null){
							Toast.makeText(getActivity(),getString(R.string.all_conditions_data), Toast.LENGTH_LONG).show();
							loadFlag = false;
						}else{
							foods.addAll(search_foods);
						}
					}
					if(foods.size()>9){
						data_lv.addFooterView(moreView);
					}else{
						if(foods.size()!=0){
							data_lv.addFooterView(footer_view);
						}
					}
					foodAdapter = new FoodListAdapter();
					data_lv.setAdapter(foodAdapter);
					/**
					 * 加载更多
					 * 如果add_course不为空，设置listview选中项为菜品的数量减去加载到的菜品数量
					 * 如果为空则设置Listview默认选中项为菜品的总数量
					 */
					if(search_foods!=null){
						data_lv.setSelection(foods.size()-search_foods.size());
					}else{
						data_lv.setSelection(foods.size());
					}
					data_lv.setOnItemClickListener(foodLv_listener);  				
				}else{
					Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
				load_btn.setVisibility(View.VISIBLE);
				load_pb.setVisibility(View.GONE);
				
			}			
			@Override
			public String getParameters() {
				return search_auto.getText().toString();
			}
		});
	}
//食材列表点击事件
	private OnItemClickListener foodLv_listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {	
			if(foods != null && position >= 0 && position < foods.size()){
			((BaseActivity)getActivity()).addFragment(FoodInfoFragment.newInstance(foods.get(position)),
					FoodInfoFragment.TAG, R.id.activity_main_left_linear);
			}
		}
	};
//食材列表点击事件
	private OnItemClickListener courseLv_listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		//	setcondition_layout.setVisibility(View.GONE);
			((BaseActivity) getActivity()).addFragment(CourseInfoFragment
					.newInstance(courses.get(arg2)),
					CourseInfoFragment.TAG, R.id.activity_main_left_linear);
		}
	};
	//根据条件查找食材
	private void searchFoodByParam(){
		foodCondition.setPage(page);
		final ProgressDialog pd = ProgressDialog.show(getActivity(), null,getString(R.string.search_course));
		service.fetchFoodByParam(new TaskCallBack<FoodCondition, MessageModel<List<Food>>>() {			
			@Override
			public void postExecute(MessageModel<List<Food>> result) {
				List<Food>search_foods = new ArrayList<Food>();
				if(result.isFlag()){
					search_foods = result.getData();
					data_lv.removeFooterView(moreView);
					data_lv.removeFooterView(footer_view);
					if(page == 1){
						if(search_foods!=null){
							foods.addAll(search_foods);
						}else{
							Toast.makeText(getActivity(),getString(R.string.no_conditions_data), Toast.LENGTH_LONG).show();
						}
					}else{
						if(search_foods==null){
							Toast.makeText(getActivity(),getString(R.string.all_conditions_data), Toast.LENGTH_LONG).show();
							loadFlag = false;
						}else{
							foods.addAll(search_foods);
						}
					}
					if(foods.size()>9){
						data_lv.addFooterView(moreView);
					}else{
						if(foods.size()!=0){
							data_lv.addFooterView(footer_view);
						}
					}
					foodAdapter = new FoodListAdapter();
					data_lv.setAdapter(foodAdapter);
					/**
					 * 加载更多
					 * 如果add_course不为空，设置listview选中项为菜品的数量减去加载到的菜品数量
					 * 如果为空则设置Listview默认选中项为菜品的总数量
					 */
					if(search_foods!=null){
						data_lv.setSelection(foods.size()-search_foods.size());
					}else{
						data_lv.setSelection(foods.size());
					}
					data_lv.setOnItemClickListener(foodLv_listener);  				
				}else{
					Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
				load_btn.setVisibility(View.VISIBLE);
				load_pb.setVisibility(View.GONE);
			}
			
			@Override
			public FoodCondition getParameters() {
				return foodCondition;
			}
		});
	}
	private void searchCourseByParam(){
		courseCondition.setPage(page);
		final ProgressDialog pd = ProgressDialog.show(getActivity(), null,getString(R.string.search_course));
		service.fetchCourseByParam(new TaskCallBack<CourseCondition, MessageModel<List<Course>>>() {			
			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				List<Course> search_courses = new ArrayList<Course>();
				if(result.isFlag()){
					search_courses = result.getData();
					data_lv.removeFooterView(moreView);
					data_lv.removeFooterView(footer_view);
					if(page == 1){
						if(search_courses!=null){
							courses.addAll(search_courses);
						}else{
							Toast.makeText(getActivity(),getString(R.string.no_conditions_data), Toast.LENGTH_LONG).show();
						}
					}else{
						if(search_courses==null){
							Toast.makeText(getActivity(),getString(R.string.all_conditions_data), Toast.LENGTH_LONG).show();
							loadFlag = false;
						}else{
							courses.addAll(search_courses);
						}
					}
					if(courses.size()>9){
						data_lv.addFooterView(moreView);
					}else{
						if(courses.size()!=0){
							data_lv.addFooterView(footer_view);
						}
					}
					courseAdapter = new CourseListAdapter();
					data_lv.setAdapter(courseAdapter);
					/**
					 * 加载更多
					 * 如果add_course不为空，设置listview选中项为菜品的数量减去加载到的菜品数量
					 * 如果为空则设置Listview默认选中项为菜品的总数量
					 */
					if(search_courses!=null){
						data_lv.setSelection(courses.size()-search_courses.size());
					}else{
						data_lv.setSelection(courses.size());
					}
					data_lv.setOnItemClickListener(courseLv_listener);  				
				}else{
					Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
				load_btn.setVisibility(View.VISIBLE);
				load_pb.setVisibility(View.GONE);
			}
			
			@Override
			public CourseCondition getParameters() {
				return courseCondition;
			}
		});
	}
	//添加菜品
		private void showAddCourse(final Button click_btn, final int position) {
				View view = LayoutInflater.from(getActivity()).inflate(
						R.layout.set_meal_type, null);
				LinearLayout layout = (LinearLayout)view.findViewById(R.id.set_meal_type_layout);
				LinearLayout wheel_layout = (LinearLayout)view.findViewById(R.id.set_meal_type_wheel_layout);
				
				WheelView dateWheel = (WheelView) view
						.findViewById(R.id.set_mealtype_date_wheel_view);
				WheelView mealWheel = (WheelView) view
						.findViewById(R.id.set_mealtype_meal_wheel_view);
				dateWheel.setAdapter(new ArrayWheelAdapter(availableDateList
						.toArray()));
				mealWheel.setAdapter(new ArrayWheelAdapter(new String[] {
						Utils.MEAL_LUNCH, Utils.MEAL_DINNER }));
				dateWheel.addChangingListener(new OnWheelChangedListener() {
					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						currentMenu = availableMenuList.get(newValue);
						meal = currentMenu.getLunch();
						date = currentMenu.getMenudate();
					}
				});
				mealWheel.addChangingListener(new OnWheelChangedListener() {
					@Override
					public void onChanged(WheelView wheel, int oldValue,
							int newValue) {
						meal = (newValue == 0) ? currentMenu.getLunch()
								: currentMenu.getDinner();
					}
				});   
				dateWheel.setCurrentItem(0);
				currentMenu = availableMenuList.get(0);
				date = currentMenu.getMenudate();
				meal = currentMenu.getLunch();
				
				int [] location = new int[2];
				click_btn.getLocationOnScreen(location);
				final PopupWindow popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				popupWindow.setBackgroundDrawable(new BitmapDrawable());
				popupWindow.setFocusable(true);
				popupWindow.setWidth((int)getResources().getDimension(R.dimen.popupWindow_height));
				popupWindow.setHeight((int)getResources().getDimension(R.dimen.popupWindow_height));
				popupWindow.setOutsideTouchable(true);
				LinearLayout.LayoutParams lp;
				if(data_lv.getHeight()-location[1]<370){
					popupWindow.showAtLocation(click_btn,Gravity.LEFT|Gravity.TOP,20,location[1]-popupWindow.getHeight()+click_btn.getHeight()/2);
					lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							lp.setMargins((int)getResources().getDimension(R.dimen.margin_popupWindow_left),
									(int)getResources().getDimension(R.dimen.margin_popupWindow_top_up),
									(int)getResources().getDimension(R.dimen.margin_popupWindow_right),
									0);
					layout.setBackgroundResource(R.drawable.add_wheel_layout_down_bg);
				}else{
					popupWindow.showAtLocation(click_btn,Gravity.LEFT|Gravity.TOP,20,location[1]);
					layout.setBackgroundResource(R.drawable.add_wheel_layout_up_bg);
					lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					lp.setMargins((int)getResources().getDimension(R.dimen.margin_popupWindow_left),
							(int)getResources().getDimension(R.dimen.margin_popupWindow_top_down),
							(int)getResources().getDimension(R.dimen.margin_popupWindow_right),
							0);
				}
				wheel_layout.setLayoutParams(lp);
				Button addBtn = (Button) view
						.findViewById(R.id.set_mealtype_meal_add_btn);
				addBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						List<Course> cList = Utils.convertMealCourse(meal.getItems());
						if(Utils.listContains(courses.get(position), cList)){
							Toast.makeText(getActivity(),
									R.string.not_allowed_add_same_course, Toast.LENGTH_LONG)
									.show();
						}else{
							popupWindow.dismiss();
							addCourse(click_btn, position);
						}

					}
				});			
			
		}
	//添加菜品
	private void addCourse(final Button click_btn, int position) {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "", getString(R.string.adding_course));
		pd.setCancelable(true);
		MealCourse mc = new MealCourse();
		mc.setCourse(courses.get(position));
		mc.setQuantity(-1);
		mc.setUnit(courses.get(position).getUnit());
		service.addCourseToMeal(date, mc,
				new TaskCallBack<Meal, MessageModel<Course>>() {
					@Override
					public void postExecute(MessageModel<Course> result) {
						if (result.isFlag()) {
							Toast.makeText(getActivity(), R.string.success_add_course,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getActivity(), result.getMessage(),
									Toast.LENGTH_LONG).show();
						}
						pd.dismiss();
					}

					@Override
					public Meal getParameters() {
						return meal;
					}
				});
	}
	private OnClickListener added_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(),getString(R.string.added_course), Toast.LENGTH_LONG)
					.show();
		}
	};
	private OnTouchListener dataLv_touch = new OnTouchListener() {		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			setcondition_layout.setVisibility(View.GONE);
			return false;
		}
	};
}
