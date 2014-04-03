package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.List;
import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SetFruitActivity;
import com.syt.health.kitchen.SetMainFoodActivity;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.fragment.HealthBibleFragment.FoodHolder;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseCondition;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.GenerateCondition;
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
import com.syt.health.kitchen.widget.OnWheelChangedListener;
import com.syt.health.kitchen.widget.QuickAction;
import com.syt.health.kitchen.widget.WheelView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FruitAddFragment extends Fragment {

	public static final String TAG = "FruitAddFragment";
	
	private List<Course> courses = new ArrayList<Course>();
	private List<Food> foods = new ArrayList<Food>();
	private ListView course_lv;
	private FoodListAdapter adapter;
	private Meal meal;
	private String date;
	private boolean isAll = false;

	private List<Food> exist_course = new ArrayList<Food>();
	private ServiceImpl service;
	private List<String> healthcondition;
	
	private CourseCondition condition = new CourseCondition();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		service = ((BaseActivity) getActivity()).getService();
		meal = service.getCurrentMenu()
				.getFruit();
		
		date = service.getCurrentMenu().getMenudate();
		try{
			healthcondition = service.getCurrentMenu().getSmartParams().getHealthcondition();
		}catch(RuntimeException re){
			healthcondition = new ArrayList<String>();
		}
		
		List<String> types = new ArrayList<String>();
		types.add(Utils.COURSE_CONDITION_FRUIT);
		condition.setCoursetype(types);
		condition.setHealthcondition(healthcondition);
		condition.setPage(-1);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fruit_add,
				container, false);
		init(view);

		((NoteActivity) getActivity()).addStep();
		return view;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void init(View view) {
		course_lv = (ListView) view
				.findViewById(R.id.fragment_fruit_listview);
		course_lv.setDivider(null);
		course_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				((BaseActivity) getActivity()).addFragment(FoodInfoFragment
						.newInstance((ArrayList<Food>) foods, position),
						FoodInfoFragment.TAG, R.id.activity_main_left_linear);
			}
		});
		
		CheckedTextView healthBtn = (CheckedTextView) view.findViewById(R.id.fragment_fruit_add_advice_btn);
		healthBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isAll == ((CheckedTextView)v).isChecked()){
					isAll = false;
					searchFruit();
				}
			}
		});
		CheckedTextView allBtn = (CheckedTextView) view.findViewById(R.id.fragment_fruit_add_all_btn);
		allBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isAll != ((CheckedTextView)v).isChecked()){
					isAll = true;
					searchFruit();
				}
			}
		});
		
		adapter = new FoodListAdapter();
		course_lv.setAdapter(adapter);
		
		searchFruit();

	}


	private void searchFruit() {
		if(!isAll){
			condition.setHealthcondition(healthcondition);
		}else{
			condition.setHealthcondition(null);
		}
		
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "", "正在获取适宜水果列表...");
		
		service.fetchCourseByParam(new TaskCallBack<CourseCondition, MessageModel<List<Course>>>() {			
			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				if(result.isFlag()){
					if(result.getData()!=null){
						courses = result.getData();
//						foods = Utils.convertFruitCourse(courses);
						
						adapter.notifyDataSetChanged();
					}
				}else{
					Toast.makeText(getActivity(),result.getMessage(), Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
			
			@Override
			public CourseCondition getParameters() {
				return condition;
			}
		});
		
	}

	/**
	 * 绑定fargment
	 * 
	 * @param meal
	 * @param date
	 * @return
	 */
	public static FruitAddFragment newInstance() {
		FruitAddFragment fragment = new FruitAddFragment();
		return fragment;
	}


	private OnClickListener added_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(), "您已经添加过该道菜", Toast.LENGTH_LONG)
					.show();
		}
	};

	class FoodListAdapter extends BaseAdapter {
		
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
		public View getView(int position, View convertView, ViewGroup parent) {
			FoodHolder h;
			if (convertView == null) {
				h = new FoodHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.food_lv_item, null);
				h.header_layout = (LinearLayout) convertView
						.findViewById(R.id.food_lv_header_layout);
				h.food_iv = (ImageView) convertView
						.findViewById(R.id.food_lv_photo_iv);
				h.name_tv = (TextView) convertView
						.findViewById(R.id.food_lv_name_tv);
				h.info_tv = (TextView) convertView
						.findViewById(R.id.food_lv_info_tv);
				h.middle_view = (View) convertView
						.findViewById(R.id.food_lv_middle_view);
				convertView.setTag(h);
			} else {
				h = (FoodHolder) convertView.getTag();
			}
			if (position == 0) {
				h.header_layout.setVisibility(View.VISIBLE);
			} else {
				h.header_layout.setVisibility(View.GONE);
			}

			Course course = courses.get(position);
			// 当courses的size小于9的时候 添加一个footer
			if (courses.size() <= 9) {
				if (position == courses.size() - 1) {
					h.middle_view.setVisibility(View.GONE);
				} else {
					h.middle_view.setVisibility(View.VISIBLE);
				}
			}
			// 加载食材图片
			((BaseActivity) getActivity()).getmImageFetcher().loadImage(
					course.getListPicUrl(), h.food_iv);
			h.name_tv.setText(course.getName());
				h.info_tv.setText("标准热量:" + course.getCalories() + "千卡");
			return convertView;
		}
	}

	class FoodHolder {
		LinearLayout header_layout;
		ImageView food_iv;
		TextView name_tv;
		TextView info_tv;
		View middle_view;
	}

	@Override
	public void onResume() {
		meal = service.getCurrentMenu().getFruit();
		exist_course = Utils.convertFruitCourse(meal.getItems());
		adapter.notifyDataSetChanged();
		super.onResume();
	}

}
