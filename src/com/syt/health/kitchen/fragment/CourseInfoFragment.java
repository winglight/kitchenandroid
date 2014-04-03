package com.syt.health.kitchen.fragment;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.customview.StickyScrollView;
import com.syt.health.kitchen.json.CookPractice;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.ImageFetcher;
import com.syt.health.kitchen.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class CourseInfoFragment extends Fragment {
	public final static String TAG = "COURSEINFOFRAGMENT";
	private static final String COURSE_KEY="MEAL";
	private static final String COURSES_KEY="MEALS";
	private TextView courseName_tv;
	private ImageView courseImg;
	private TextView page_tv;
	//private Meal meal;
	private LinearLayout info_layout;
	private LinearLayout jieshao_layout;
	private LinearLayout shicai_layout;
	private LinearLayout step_layout;
	private int currentPosition;
	private Course course;
	private ServiceImpl service;
	private List<Course>courses = new ArrayList<Course>();
	private ImageFetcher imageFetcher;
	private TextView step_text;

	private boolean isHideNavBar;
	
	private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		courses = (List<Course>)getArguments().get(COURSE_KEY);
//		currentPosition = (Integer)getArguments().get("index");
//		course = courses.get(currentPosition);
		courses = (List<Course>) ((getArguments() != null) ? getArguments().get(
				COURSES_KEY) : null);

		if (courses != null) {
			currentPosition = (Integer) ((getArguments() != null) ? getArguments()
					.get("index") : 0);
			course = courses.get(currentPosition);

			isHideNavBar = false;
		} else {
			course = (Course) ((getArguments() != null) ? getArguments().get(
					COURSE_KEY) : null);
			isHideNavBar = true;
		}
		service = ((BaseActivity)getActivity()).getService();
		
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(!isHideNavBar){
			params.setMargins(22, 0, 0, 20);
		}else{
			params.setMargins(22, 30, 0, 22);
		}
		
		
		View view = inflater.inflate(R.layout.fragment_course_info, container, false);
		init(view);
         
		((NoteActivity) getActivity()).addStep();
		
		getNewCourse(course.getId());
		
		return view;
	}
	/**
	 * 初始化控件
	 * @param view
	 */
	private void init(View view){
		LinearLayout layout = (LinearLayout)view.findViewById(R.id.fragment_course_info_title_layout);
		final StickyScrollView scroll = (StickyScrollView)view.findViewById(R.id.fragment_course_info_scroll);
		
		if (isHideNavBar) {
			layout.setVisibility(View.GONE);
		}else{
			page_tv = (TextView)view.findViewById(R.id.fragment_course_info_page_tv);
			page_tv.setText(currentPosition+1+"/"+courses.size());
			Button preBtn = (Button)view.findViewById(R.id.fragment_course_info_up_button);
			preBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(currentPosition > 0){
						currentPosition--;
						course = courses.get(currentPosition);
						getNewCourse(course.getId());
						scroll.scrollTo(0, 0);
					}
					
				}
			});
			Button nextBtn = (Button)view.findViewById(R.id.fragment_course_info_next_button);
			nextBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(currentPosition < courses.size()-1){
						currentPosition++;
						course = courses.get(currentPosition);
						getNewCourse(course.getId());
						scroll.scrollTo(0, 0);
					}
					
				}
			});
		}
		
		
		courseName_tv = (TextView)view.findViewById(R.id.fragment_course_info_coursename_tv);
		courseName_tv.setText(course.getName());
		
		courseImg = (ImageView)view.findViewById(R.id.fragment_course_info_course_image);
		
		scroll.setLayoutParams(params);
		info_layout = (LinearLayout)view.findViewById(R.id.fragment_course_info_layout);
		jieshao_layout = (LinearLayout)view.findViewById(R.id.fragment_course_info_jieshao_layout);
		shicai_layout = (LinearLayout)view.findViewById(R.id.fragment_course_info_shicai_layout);
		step_layout = (LinearLayout)view.findViewById(R.id.fragment_course_info_step_layout);
		step_text = (TextView)view.findViewById(R.id.fragment_course_info_step_txt);
		
		Button infoIntroBtn = (Button)view.findViewById(R.id.fragment_course_info_intro_btn);
		infoIntroBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scroll.scrollTo(0, ((View)jieshao_layout.getParent()).getTop()-info_layout.getHeight());
				
			}
		});
		Button infoFoodBtn = (Button)view.findViewById(R.id.fragment_course_info_food_btn);
		infoFoodBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scroll.scrollTo(0, ((View)shicai_layout.getParent()).getTop()-info_layout.getHeight());
				
			}
		});
		Button infoStepBtn = (Button)view.findViewById(R.id.fragment_course_info_step_btn);
		infoStepBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scroll.scrollTo(0, ((View)step_layout.getParent()).getTop()-info_layout.getHeight());
				
			}
		});
		Button infoTopBtn = (Button)view.findViewById(R.id.fragment_course_info_top_btn);
		infoTopBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scroll.scrollTo(0, 0);
				
			}
		});
	}
	/**
	 * 绑定fragment
	 * @param meal
	 * @param index
	 * @return
	 */
	public static CourseInfoFragment newInstance(ArrayList<Course> courses,int index){
		CourseInfoFragment fragment  = new CourseInfoFragment();
		Bundle bundle = new Bundle(); 
		bundle.putSerializable(COURSES_KEY,courses);
		bundle.putInt("index", index);
		fragment.setArguments(bundle);
		return fragment;
	}
	public static CourseInfoFragment newInstance(Course course) {
		CourseInfoFragment fragment = new CourseInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(COURSE_KEY, course);
		fragment.setArguments(bundle);
		return fragment;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		imageFetcher = ((BaseActivity) activity).getmImageFetcher();
	}
	/**
	 * 初始化数据
	 * @param course
	 */
	private void initializeData(Course course){
		if(getActivity() == null) return;
		
		courseName_tv.setText(course.getName());	
		imageFetcher.loadImage(course.getPicurl(),
				courseImg);
		jieshao_layout.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		TextView course_type = new TextView(getActivity());
		course_type.setText(getString(R.string.type)+course.getCoursecond());
		course_type.setLayoutParams(params);
		course_type.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_type.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_type);
		TextView course_taste = new TextView(getActivity());
		course_taste.setText(getString(R.string.taste)+course.getTaste());
		course_taste.setLayoutParams(params);
		course_taste.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_taste.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_taste);
		TextView course_calories = new TextView(getActivity());
		course_calories.setText(getString(R.string.heat)+course.getCalories() + "千卡");
		course_calories.setLayoutParams(params);
		course_calories.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_calories.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_calories);
		TextView course_effectivity = new TextView(getActivity());
		
		GenerateCondition smcp;
		if(service.getCurrentMenu() == null || (getActivity() instanceof NoteActivity && ((NoteActivity)getActivity()).isHealthBible())){
			smcp = service.getCurrentUser().getObjSmartParams();
		}else{
			smcp = service.getCurrentMenu().getSmartParams();
		}
		String shiyi = Utils.arrayIntoString(course.getEffectivity());
		List<String> strList = course.getGoodDesc(smcp.getHealthcondition());
		
		String tmpHealth = (getActivity() instanceof NoteActivity) ? ((NoteActivity) getActivity())
				.getCurrentHealthCondition() : null;
		if (tmpHealth != null) {
			Utils.listAdd(tmpHealth, strList);
		}
		
		for(String spShiyi : strList){
			if(shiyi.contains(spShiyi)){
				shiyi = shiyi.replace(spShiyi, "<span style=\"background-color:#f88855\"><font color=\"#f88855\">" + spShiyi + "</font></span>");
			}
		}
		course_effectivity.setText(Html.fromHtml("适宜人群：<BR>"+shiyi));
		course_effectivity.setLayoutParams(params);
		course_effectivity.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_effectivity.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_effectivity);
		TextView course_incompatible = new TextView(getActivity());
		
		String buyi = Utils.arrayIntoString(course.getIncompatible());
		strList = course.getBadDesc(smcp.getHealthcondition());
		
		if (tmpHealth != null) {
			strList.clear();
			strList.add(tmpHealth);
		}
		
		for(String spBuyi : strList){
			if(buyi.contains(spBuyi)){
				buyi = buyi.replace(spBuyi, "<span style=\"background-color:#f88855\"><font color=\"#f88855\">" + spBuyi + "</font></span>");
			}
		}
		course_incompatible.setText(Html.fromHtml("不宜人群：<BR>"+ buyi));
		course_incompatible.setLayoutParams(params);
		course_incompatible.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_incompatible.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_incompatible);
		
		TextView course_cookstep = new TextView(getActivity());
		course_cookstep.setText(getString(R.string.wayOfcooking)+Utils.arrayIntoString(course.getCookmethod()));
		course_cookstep.setLayoutParams(params);
		course_cookstep.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_cookstep.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_cookstep);
		
		TextView course_preparetime = new TextView(getActivity());
		course_preparetime.setText(getString(R.string.precooktime)+course.getPrecooktime());
		course_preparetime.setLayoutParams(params);
		course_preparetime.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_preparetime.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_preparetime);
		
		TextView course_cooktime = new TextView(getActivity());
		course_cooktime.setText(getString(R.string.totalcooktime)+course.getTotalcooktime());
		course_cooktime.setLayoutParams(params);
		course_cooktime.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
		course_cooktime.setTextColor(getResources().getColor(android.R.color.black));
		jieshao_layout.addView(course_cooktime);
//		LinearLayout footer = new LinearLayout(getActivity());
//		footer.setBackgroundResource(R.drawable.cell_middle_bg);
//		jieshao_layout.addView(footer);
		
		List<CourseFood> courseFoods = course.getItems();
		String zhuliao = CourseFood.FOODTYPE_ZHULIAO + ":\n";
		String fuliao = CourseFood.FOODTYPE_FULIAO + ":\n";
		String peiliao = CourseFood.FOODTYPE_PEILIAO + ":\n";
//		int i = 1;
		for (CourseFood courseFood : courseFoods) {
			String food = courseFood.getFood().getName();
			food += "\t" + (courseFood.getQuantity()==0?"":String.valueOf(courseFood.getQuantity())) + courseFood.getFoodunit() + "\n";
			if(CourseFood.FOODTYPE_ZHULIAO.equals(courseFood.getFoodtype())){
				zhuliao += food;
			}else if(CourseFood.FOODTYPE_FULIAO.equals(courseFood.getFoodtype())){
				fuliao += food;
			}else{
				peiliao += food;
			}
//			i++;
		}
			shicai_layout.removeAllViews();
			TextView shicai_tv = new TextView(getActivity());
			shicai_tv.setText(zhuliao+(fuliao.length()>4?fuliao:"")+(peiliao.length()>4?peiliao:""));
			shicai_tv.setLayoutParams(params);
			shicai_tv.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
			shicai_tv.setTextColor(getResources().getColor(android.R.color.black));
			shicai_layout.addView(shicai_tv);
		
		List<CookPractice> cookPractices = course.getCookpractice();
		step_layout.removeAllViews();
		
		if(cookPractices!=null&&cookPractices.size()>0){
			step_text.setText(R.string.step);
			for (CookPractice cookPractice : cookPractices) {
				TextView cookPractice_tv = new TextView(getActivity());
				cookPractice_tv.setText(cookPractice.getStep()+":"+cookPractice.getDesc());
				cookPractice_tv.setLayoutParams(params);
				cookPractice_tv.setTextSize(getResources().getDimension(R.dimen.food_info_text_size));
				cookPractice_tv.setTextColor(getResources().getColor(android.R.color.black));
				step_layout.addView(cookPractice_tv);
			}
		}else{
			step_text.setText(R.string.step_no_data);
		}
	}
	/**
	 * 根据course的id查找对应的course
	 * @param id
	 */
	private void getNewCourse(final String id){
		 service.getCourseById(new TaskCallBack<String, MessageModel<Course>>() {			
				@Override
				public void postExecute(MessageModel<Course> result) {
					if(result.isFlag()){
						course = result.getData();
						initializeData(course);
						if(page_tv != null){
							page_tv.setText(currentPosition+1+"/"+courses.size());
						}
					}
				}			
				@Override
				public String getParameters() {
					return id;
				}
			});
	}
}
