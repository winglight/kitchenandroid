package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.List;
import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SetMainFoodActivity;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseCondition;
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
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FoodCourseFragment extends Fragment {

	public static final String TAG = "FoodCourseFragment";
	
	private ServiceImpl service;

	// Food name
	private String foodName;
	private List<String> availableDateList = new ArrayList<String>();
	private List<Menu> availableMenuList = new ArrayList<Menu>();
	private Menu currentMenu;

	private HorizontalScrollView scroll;

	private List<Course> courses = new ArrayList<Course>();
	private ListView course_lv;
	private CourseListAdapter adapter;
	private Meal meal;
	private View moreView;
	private Button load_btn;
	private ProgressBar load_pb;

	private int page = 1;
	private View footer_view;
	private TextView foodCourseTxt;
	
	private boolean loadFlag = true;//是否能够加载 true是false否

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		service = ((BaseActivity) getActivity()).getService();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_food_course,
				container, false);
		init(view);

		((NoteActivity) getActivity()).addStep();
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		availableMenuList = service.getMenuIn7Days();
		if (availableMenuList != null) {
			availableDateList.clear();
			for (Menu menu : availableMenuList) {
				availableDateList.add(DateUtils.getMonthDay(menu.getMenudate(),
						false));
			}
		}
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void init(View view) {
		footer_view = LayoutInflater.from(getActivity()).inflate(
				R.layout.footer, null);
		moreView = LayoutInflater.from(getActivity()).inflate(
				R.layout.moredata, null);
		load_pb = (ProgressBar) moreView.findViewById(R.id.moredata_pb);
		load_btn = (Button) moreView.findViewById(R.id.moredata_load_btn);
		load_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(loadFlag){
				load_btn.setVisibility(View.GONE);
				load_pb.setVisibility(View.VISIBLE);

					page++;
					searchFoodCourse();
				}else{
					Toast.makeText(getActivity(), getString(R.string.all_conditions_data), Toast.LENGTH_LONG).show();
				}
			}
		});

		foodCourseTxt = (TextView) view
				.findViewById(R.id.fragment_foodcourse_foodcourse_txt);

		course_lv = (ListView) view
				.findViewById(R.id.fragment_foodcourse_course_lv);
		course_lv.setDivider(null);
		course_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				((BaseActivity) getActivity()).addFragment(CourseInfoFragment
						.newInstance((ArrayList<Course>) courses, position),
						CourseInfoFragment.TAG, R.id.activity_main_left_linear);
			}
		});

			foodCourseTxt.setVisibility(View.VISIBLE);
			foodCourseTxt.setText(getResources().getString(
					R.string.food_courses, foodName));

			searchFoodCourse();
	}


	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public static FoodCourseFragment newInstance(String foodName) {
		FoodCourseFragment fragment = new FoodCourseFragment();
		fragment.setFoodName(foodName);
		return fragment;
	}

	/**
	 * 重写listview适配器
	 * 
	 * @author ChengFaner
	 * 
	 */
	class CourseListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (courses != null) {
				return courses.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (courses != null) {
				return courses.get(position);
			}
			return 0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			Holder h;
			if (convertView == null) {
				h = new Holder();
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
				h.header_layout = (LinearLayout) convertView
						.findViewById(R.id.add_course_lv_header_layout);
				convertView.setTag(h);
			} else {
				h = (Holder) convertView.getTag();
			}
			if (position == 0) {
				h.header_layout.setVisibility(View.VISIBLE);
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

				if (course.getEffectivity() != null
						&& course.getEffectivity().size() > 0) {
					h.good_iv
							.setBackgroundResource(R.drawable.good_button_state);
					h.good_iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							QuickAction mAction = new QuickAction(getActivity());
							ActionItem item = new ActionItem(0, Utils
									.arrayIntoString(course.getEffectivity()));
							mAction.addActionItem(item);
							mAction.show(v);
						}
					});
				} else {
					h.good_iv.setBackgroundResource(R.drawable.good_no_bg);
					h.good_iv.setOnClickListener(null);
				}

				if (course.getIncompatible() != null
						&& course.getIncompatible().size() > 0) {
					h.bad_iv.setBackgroundResource(R.drawable.bad_button_state);
					h.bad_iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							QuickAction mAction = new QuickAction(getActivity());
							ActionItem item = new ActionItem(0, Utils
									.arrayIntoString(course.getIncompatible()));
							mAction.addActionItem(item);
							mAction.show(v);
						}
					});
				} else {
					h.bad_iv.setBackgroundResource(R.drawable.bad_no_bg);
					h.bad_iv.setOnClickListener(null);
				}
				if(Utils.COURSE_CONDITION_ZZF.contains(course.getCoursecond())){
					h.add_btn.setVisibility(View.GONE);
				}else{
					h.add_btn.setVisibility(View.VISIBLE);
				h.add_btn
						.setBackgroundResource(R.drawable.add_lv_course_button_state);
				h.add_btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
							// show selector of date and meal type
							if (availableDateList.size() > 0) {
								showSetCondition((Button) v, position);
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
				}

			((BaseActivity) getActivity()).getmImageFetcher().loadImage(
					course.getPicurl(), h.course_iv);
			h.courseName_tv.setText(course.getName());
			h.courseInfo_tv.setText(course.getCoursecond() + "   "
					+ course.getCalories() + "千卡");
			return convertView;
		}
	}

	class Holder {
		LinearLayout header_layout;
		ImageView course_iv;
		TextView courseName_tv;
		ImageView good_iv;
		ImageView bad_iv;
		Button add_btn;
		TextView courseInfo_tv;
		View middle_view;
	}

	private void showSetCondition(final Button click_btn, final int position) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.set_meal_type, null);
			LinearLayout playout = (LinearLayout)view.findViewById(R.id.set_meal_type_layout);
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
			if(course_lv.getHeight()-location[1]<370){
				popupWindow.showAtLocation(click_btn,Gravity.LEFT|Gravity.TOP,20,location[1]-popupWindow.getHeight()+click_btn.getHeight()/2);
				lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						lp.setMargins((int)getResources().getDimension(R.dimen.margin_popupWindow_left),
								(int)getResources().getDimension(R.dimen.margin_popupWindow_top_up),
								(int)getResources().getDimension(R.dimen.margin_popupWindow_right),
								0);
				playout.setBackgroundResource(R.drawable.add_wheel_layout_down_bg);
			}else{
				popupWindow.showAtLocation(click_btn,Gravity.LEFT|Gravity.TOP,20,location[1]);
				playout.setBackgroundResource(R.drawable.add_wheel_layout_up_bg);
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
					List<Course> cList = Utils.convertMealCourse(meal
							.getItems());
					if (Utils.listContains(courses.get(position), cList)) {
						Toast.makeText(getActivity(),
								R.string.not_allowed_add_same_course,
								Toast.LENGTH_LONG).show();
					} else {
						popupWindow.dismiss();
						addCourse(click_btn, position);
					}
				}
			});


	}


	/**
	 * Get courses of food
	 */
	private void searchFoodCourse() {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				getString(R.string.search_course));
		service.fetchCourseByParam(new TaskCallBack<CourseCondition, MessageModel<List<Course>>>() {
			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				List<Course> add_course = new ArrayList<Course>();
				course_lv.removeFooterView(moreView);
				course_lv.removeFooterView(footer_view);
				if (result.isFlag()) {
					add_course = result.getData();

					if (add_course == null) {
						if(page == 1){
						new AlertDialog.Builder(getActivity())
						.setMessage(result.getMessage())
						.setPositiveButton(
								getResources().getString(
										R.string.know_dialog),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										((BaseActivity) getActivity())
												.backFragment(FoodCourseFragment.this);
									}
								}).show();
						}else{
							Toast.makeText(getActivity(),getString(R.string.all_conditions_data), Toast.LENGTH_LONG).show();
							loadFlag = false;
						}
					} else {
						courses.addAll(add_course);

					}
					adapter = new CourseListAdapter();
					if (courses.size() > 9) {
						course_lv.addFooterView(moreView);
					} else {
						if (courses.size() != 0) {
							course_lv.addFooterView(footer_view);
						}
					}
					course_lv.setAdapter(adapter);

					if (add_course != null) {
						course_lv.setSelection(courses.size()
								- add_course.size());
					} else {
						course_lv.setSelection(courses.size());
					}
				} else {
					 Toast.makeText(getActivity(), result.getMessage(),
					 Toast.LENGTH_LONG).show();
					
				}
				load_btn.setVisibility(View.VISIBLE);
				load_pb.setVisibility(View.GONE);
				pd.dismiss();
			}

			@Override
			public CourseCondition getParameters() {
				CourseCondition cc = CourseCondition.newByFoodInstance();
				
				String tmpHealth = (getActivity() instanceof NoteActivity) ? ((NoteActivity) getActivity())
						.getCurrentHealthCondition() : null;
				if (tmpHealth != null) {
					ArrayList<String> list = new ArrayList<String>();
					list.add(tmpHealth);
					
					cc.setHealthcondition(list);
				}
				
				cc.setFoodName(foodName);
				cc.setPage(page);
				return cc;
			}
		});
	}

	private void addCourse(final Button click_btn, int position) {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",getString(R.string.adding_course));
		MealCourse mc = new MealCourse();
		mc.setCourse(courses.get(position));
		mc.setQuantity(-1);
		mc.setUnit(courses.get(position).getUnit());
		service.addCourseToMeal(currentMenu.getMenudate(), mc,
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


}
