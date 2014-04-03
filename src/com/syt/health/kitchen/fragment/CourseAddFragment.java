package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.List;
import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SetMainFoodActivity;
import com.syt.health.kitchen.fragment.MealFragment.ClickListenerForScrolling;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

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

public class CourseAddFragment extends Fragment {
	public static final String TAG_ADD = "COURSE_ADD";
	public static final String TAG_MAIN_FOOD = "MAIN_FOOD";
	public static final String TAG_ZAO_DIAN = "ZAO_DIAN";

	private LinearLayout type_layout;
	private CheckedTextView type_btn;
	private CheckedTextView taste_btn;
	private CheckedTextView health_btn;
	private String tag;
	private ServiceImpl service;

	// selected conditions
	private List<String> tast_types = new ArrayList<String>();
	private List<String> cond_types = new ArrayList<String>();
	private List<String> health_types = new ArrayList<String>();

	// all of prepared conditions
	private List<String> tastes = new ArrayList<String>();
	private List<String> courseConditions = new ArrayList<String>();
	private List<String> healthcondition;

	private HorizontalScrollView scroll;

	private List<Course> courses = new ArrayList<Course>();
	private ListView course_lv;
	private CourseListAdapter adapter;
	private Meal meal;
	private View moreView;
	private Button load_btn;
	private ProgressBar load_pb;
	private GenerateCondition conditionParam;

	private List<Course> exist_course = new ArrayList<Course>();
	private int page = 1;
	private View footer_view;
	private int type;
	private int type_flag = -1;// -1:hide type layout;0-show taste layout;1-show
								// condition layout;2-show health layout
	private boolean loadFlag = true;// 是否能够加载 true是false否

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tag = getTag();
		service = ((BaseActivity) getActivity()).getService();
		type = (getArguments() != null) ? getArguments().getInt(
				Utils.MEAL_TYPE_KEY) : 0;

		meal = service.getCurrentMenu().getMealByType(type);
		tastes = service.getAllTaste();
		courseConditions = service.getAllCourseCondition();
		conditionParam = service.getCurrentUser().getObjSmartParams();
		healthcondition = conditionParam.getHealthcondition();
		if (healthcondition != null) {
			health_types.addAll(healthcondition);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_course_add, container,
				false);
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
		SharedPreferences sp = getActivity().getSharedPreferences("sp_first",0);
		LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.activity_main_help_layout);
		if(sp.getBoolean(Utils.ADD_COURSE_FALG, true)){
			switch (type) {
			case Utils.BREAKFAST:
				Utils.addImageView(getActivity(), layout, R.drawable.help_breakfast_add_course,0,Utils.ADD_COURSE_FALG, sp);
				break;
			case Utils.LUNCH:
				Utils.addImageView(getActivity(), layout, R.drawable.help_lunch_add_course,0,Utils.ADD_COURSE_FALG, sp);
				break;
			case Utils.DINNER:
				Utils.addImageView(getActivity(), layout, R.drawable.help_dinner_add_course,0,Utils.ADD_COURSE_FALG, sp);
				break;
			}
		}
		
		footer_view = LayoutInflater.from(getActivity()).inflate(
				R.layout.footer, null);
		moreView = LayoutInflater.from(getActivity()).inflate(
				R.layout.moredata, null);
		load_pb = (ProgressBar) moreView.findViewById(R.id.moredata_pb);
		load_btn = (Button) moreView.findViewById(R.id.moredata_load_btn);
		load_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {  
				if (loadFlag) {
					load_btn.setVisibility(View.GONE);
					load_pb.setVisibility(View.VISIBLE);

					page++;
					fetchCourse();
				} else {
					Toast.makeText(getActivity(),getString(R.string.all_conditions_data),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		course_lv = (ListView) view
				.findViewById(R.id.fragment_courseadd_course_lv);
		course_lv.setDivider(null);
		course_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				((BaseActivity) getActivity()).addFragment(
						CourseInfoFragment.newInstance(courses.get(position)),
						CourseInfoFragment.TAG, R.id.activity_main_left_linear);
			}
		});

		type_layout = (LinearLayout) view
				.findViewById(R.id.fragment_courseadd_type_linearlayout);

		type_btn = (CheckedTextView) view
				.findViewById(R.id.fragment_courseadd_type_btn);

		scroll = (HorizontalScrollView) view
				.findViewById(R.id.fragment_courseadd_type_scroll);

		type_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetBtns();

				type_layout.removeAllViews();
				if (type_flag != 1) {
					scroll.setVisibility(View.VISIBLE);
					scroll.bringToFront();

					v.setBackgroundResource(R.drawable.course_typebtn_active);
					v.bringToFront();
					type_flag = 1;
					((CheckedTextView) v).setChecked(true);
					initializeType(courseConditions);

					((CheckedTextView) v).setTextColor(getResources().getColor(
							R.color.selected_text_color));

				} else {
					type_btn.setBackgroundResource(R.drawable.course_typebtn_inactive);
					scroll.setVisibility(View.GONE);
					type_flag = -1;
					((CheckedTextView) v).setChecked(false);

				}

			}
		});
		taste_btn = (CheckedTextView) view
				.findViewById(R.id.fragment_courseadd_taste_btn);
		taste_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetBtns();

				if (type_flag != 0) {
					scroll.setVisibility(View.VISIBLE);
					scroll.bringToFront();

					v.setBackgroundResource(R.drawable.course_tastebtn_active);
					v.bringToFront();
					type_flag = 0;
					initializeType(tastes);

					((CheckedTextView) v).setTextColor(getResources().getColor(
							R.color.selected_text_color));

					// FrameLayout.LayoutParams params = new
					// FrameLayout.LayoutParams(
					// LayoutParams.WRAP_CONTENT,
					// LayoutParams.WRAP_CONTENT, Gravity.NO_GRAVITY);
					//
					// params.setMargins(v.getLeft() + 20, v.getBottom() -
					// 6,
					// 0, 0);

				} else {
					taste_btn
							.setBackgroundResource(R.drawable.course_tastebtn_inactive);
					scroll.setVisibility(View.GONE);
					type_flag = -1;

				}

			}

		});
		health_btn = (CheckedTextView) view
				.findViewById(R.id.fragment_courseadd_health_btn);
		health_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetBtns();

				if (type_flag != 2) {
					scroll.setVisibility(View.VISIBLE);
					scroll.bringToFront();

					v.setBackgroundResource(R.drawable.health_conditionbtn_active);
					v.bringToFront();
					type_flag = 2;
					initializeType(healthcondition);

					((CheckedTextView) v).setTextColor(getResources().getColor(
							R.color.selected_text_color));

				} else {
					health_btn
							.setBackgroundResource(R.drawable.health_conditionbtn_inactive);
					scroll.setVisibility(View.GONE);
					type_flag = -1;

				}
			}
		});
		resetBtns();
		ImageView search_btn2 = (ImageView) view
				.findViewById(R.id.fragment_courseadd_condition_search_btn);
		search_btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				courses.clear();
				page = 1;
				fetchCourse();

				resetBtns();
				type_flag = -1;
				scroll.setVisibility(View.GONE);
			}
		});

		if (tag.equals(TAG_ADD)) {

		} else {
			taste_btn.setEnabled(false);
			type_btn.setEnabled(false);

			if (type == Utils.BREAKFAST) {
				cond_types.add(Utils.COURSE_CONDITION_ZAODIAN);
				type_btn.setText(Utils.COURSE_CONDITION_ZAODIAN);
			} else {
				cond_types.add(Utils.COURSE_CONDITION_ZHUSHI);
				type_btn.setText(Utils.COURSE_CONDITION_ZHUSHI);
			}
		}

		fetchCourse();
	}

	private void resetBtns() {
		type_btn.setBackgroundResource(R.drawable.course_typebtn_inactive);
		taste_btn.setBackgroundResource(R.drawable.course_tastebtn_inactive);
		health_btn
				.setBackgroundResource(R.drawable.health_conditionbtn_inactive);

		type_btn.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
		taste_btn.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
		health_btn.setTextColor(getResources().getColor(
				R.color.unselected_text_color));
	}

	/**
	 * 获取菜品分类
	 * 
	 * @param lists
	 */
	private void initializeType(List<String> lists) {
		type_layout.removeAllViews();
		TextView btn = new TextView(getActivity());
		btn.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));
		if (type_flag == 2) {
			btn.setText(getString(R.string.all));
			btn.setTag(getString(R.string.all));
		} else {
			btn.setText(getString(R.string.unlimited));
			btn.setTag(getString(R.string.unlimited));
		}
		if ((type_flag == 0 && getString(R.string.unlimited).equals(taste_btn.getTag()))
				|| (type_flag == 1 && getString(R.string.unlimited).equals(type_btn.getTag()))
				|| (type_flag == 2 && getString(R.string.all).equals(health_btn.getTag()))) {
			btn.setTextColor(getResources().getColor(
					R.color.selected_text_color));
		} else {
			btn.setTextColor(getResources().getColor(
					R.color.unselected_text_color));
		}
		btn.setTextSize(20);
		btn.setOnClickListener(btn_listener);
		android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 10, 0);
		btn.setLayoutParams(params);
		type_layout.addView(btn);

		if (type_flag == 2) {
			btn = new TextView(getActivity());
			btn.setBackgroundColor(getResources().getColor(
					android.R.color.transparent));

			btn.setText(getString(R.string.unlimited));
			btn.setTag(getString(R.string.unlimited));
			btn.setTextSize(20);
			btn.setOnClickListener(btn_listener);
			btn.setLayoutParams(params);
			type_layout.addView(btn);
		}

		for (int i = 0; i < lists.size(); i++) {
			btn = new TextView(getActivity());
			btn.setBackgroundColor(getResources().getColor(
					android.R.color.transparent));
			btn.setText(lists.get(i));
			btn.setTag(lists.get(i));
			btn.setTextSize(20);
			btn.setPadding(5, 0, 5, 0);
			if ((type_flag == 0 && lists.get(i).equals(taste_btn.getTag()))
					|| (type_flag == 1 && lists.get(i)
							.equals(type_btn.getTag()))
					|| (type_flag == 2 && lists.get(i).equals(
							health_btn.getTag()))) {
				btn.setTextColor(getResources().getColor(
						R.color.selected_text_color));
			} else {
				btn.setTextColor(getResources().getColor(
						R.color.unselected_text_color));
			}
			btn.setLayoutParams(params);
			type_layout.addView(btn);
			btn.setOnClickListener(btn_listener);

		}
	}

	private OnClickListener btn_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String type = v.getTag().toString();
			if (type_flag == 1) {
				cond_types.clear();
				if (!type.equals(getString(R.string.unlimited))) {
					cond_types.add(type);
				}
				type_btn.setText(type);
				type_btn.setTag(type);
			} else if (type_flag == 0) {
				tast_types.clear();
				if (!type.equals(getString(R.string.unlimited))) {
					tast_types.add(type);
				}
				taste_btn.setText(type);
				taste_btn.setTag(type);
			} else {
				health_types.clear();
				if (type.equals(getString(R.string.all))) {
					health_types.addAll(healthcondition);
				} else if (type.equals(getString(R.string.unlimited))) {
					health_types.clear();
				} else {
					health_types.add(type);
				}
				health_btn.setText(type);
				health_btn.setTag(type);
			}

			for (int i = 0; i < type_layout.getChildCount(); i++) {
				TextView btn = (TextView) type_layout.getChildAt(i);
				btn.setTextColor(getResources().getColor(
						R.color.unselected_text_color));
			}
			((TextView) v).setTextColor(getResources().getColor(
					R.color.selected_text_color));

			type_flag = -1;
			type_layout.invalidate();
			scroll.setVisibility(View.GONE);
			resetBtns();
		}
	};

	/**
	 * 查找菜品
	 */
	private void fetchCourse() {

		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				getString(R.string.search_course));
		final CourseCondition courseCondition = new CourseCondition();
		courseCondition.setTaste(tast_types);
		courseCondition.setCoursecond(cond_types);
		if (type == 2 || type == 3) {
			List<String> tList = new ArrayList<String>();
			tList.add(Utils.COURSE_TYPE_ZHENGCAN);
			courseCondition.setCoursetype(tList);
		}
		courseCondition.setHealthcondition(health_types);

		courseCondition.setFiltercond("yes");

		courseCondition.setPage(page);

		service.fetchCourseByParam(new TaskCallBack<CourseCondition, MessageModel<List<Course>>>() {
			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				List<Course> add_course = new ArrayList<Course>();
				if (result.isFlag()) {
					course_lv.removeFooterView(moreView);
					course_lv.removeFooterView(footer_view);
					add_course = result.getData();

					if (add_course != null) {
						courses.addAll(add_course);
					} else {
						if (page == 1) {
							Toast.makeText(getActivity(),getString(R.string.no_conditions_data),
									Toast.LENGTH_LONG).show();
						} else {
							if (add_course == null) {
								Toast.makeText(getActivity(),getString(R.string.all_conditions_data),
										Toast.LENGTH_LONG).show();
								loadFlag = false;
							}
						}
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
					adapter.notifyDataSetChanged();

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
				return courseCondition;
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
	public static CourseAddFragment newInstance(int meal_type) {
		CourseAddFragment fragment = new CourseAddFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Utils.MEAL_TYPE_KEY, meal_type);
		fragment.setArguments(bundle);
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
			} else {
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

			final List<String> goodDesc = course.getGoodDesc(conditionParam
					.getHealthcondition());
			if (goodDesc.size() != 0) {
				h.good_iv.setBackgroundResource(R.drawable.good_button_state);
				h.good_iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						QuickAction mAction = new QuickAction(getActivity());
						ActionItem item = new ActionItem(0, Utils
								.arrayIntoString(goodDesc));
						mAction.addActionItem(item);
						mAction.show(v);
					}
				});
			} else {
				h.good_iv.setBackgroundResource(R.drawable.good_no_bg);
				h.good_iv.setOnClickListener(null);
			}

			final List<String> badDesc = course.getBadDesc(conditionParam
					.getHealthcondition());
			if (badDesc.size() != 0) {
				h.bad_iv.setBackgroundResource(R.drawable.bad_button_state);
				h.bad_iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						QuickAction mAction = new QuickAction(getActivity());
						ActionItem item = new ActionItem(0, Utils
								.arrayIntoString(badDesc));
						mAction.addActionItem(item);
						mAction.show(v);
					}
				});
			} else {
				h.bad_iv.setBackgroundResource(R.drawable.bad_no_bg);
				h.bad_iv.setOnClickListener(null);
			}
			if (!Utils.listContains(course, exist_course)) {
				h.add_btn
						.setBackgroundResource(R.drawable.add_lv_course_button_state);
				h.add_btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (Utils.COURSE_CONDITION_ZHUSHI.equals(course
								.getCoursecond())
								|| Utils.COURSE_CONDITION_ZAODIAN.equals(course
										.getCoursecond())) {
							Intent intent = new Intent(getActivity(),
									SetMainFoodActivity.class);
							intent.putExtra(Utils.DATE_KEY, service
									.getCurrentMenu().getMenudate());
							intent.putExtra(Utils.MEAL_TYPE_KEY, type);
							MealCourse mealCourse = new MealCourse();
							mealCourse.setCourse(course);
							mealCourse.setQuantity(0);
							mealCourse.setUnit(course.getUnit());
							intent.putExtra(Utils.MEALCOURSE, mealCourse);
							startActivity(intent);
						} else {
							final Button click_btn = (Button) v;
							addCourse(click_btn, position);
						}
					}
				});
			} else {
				h.add_btn.setBackgroundResource(R.drawable.added_course_bg);
				h.add_btn.setOnClickListener(added_listener);
			}

			((BaseActivity) getActivity()).getmImageFetcher().loadImage(
					course.getListPicUrl(), h.course_iv);
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

	private OnClickListener added_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(getActivity(),getString(R.string.added_course), Toast.LENGTH_LONG)
					.show();
		}
	};

	private void refreshCourse() {
		if (meal != null) {
			exist_course.addAll(Utils.convertMealCourse(meal.getItems()));
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	private void addCourse(final Button click_btn, int position) {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				getString(R.string.adding_course));
		MealCourse mc = new MealCourse();
		mc.setCourse(courses.get(position));
		mc.setQuantity(-1);
		mc.setUnit(courses.get(position).getUnit());
		service.addCourseToMeal(service.getCurrentMenu().getMenudate(), mc,
				new TaskCallBack<Meal, MessageModel<Course>>() {
					@Override
					public void postExecute(MessageModel<Course> result) {
						List<MealCourse> mealCourses = new ArrayList<MealCourse>();
						if (result.isFlag()) {
							mealCourses = meal.getItems();
							click_btn
									.setBackgroundResource(R.drawable.added_course_bg);
							click_btn.setOnClickListener(added_listener);
							Course course = result.getData();
							MealCourse mealCourse = new MealCourse();
							mealCourse.setCourse(course);
							mealCourse.setQuantity(1);
							mealCourse.setUnit(course.getUnit());
							Utils.listAdd(mealCourse, mealCourses);

							meal.setItems(mealCourses);
							refreshCourse();
							// ((NoteActivity)getActivity()).showBreakfastBv(meal.getItems().size());
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

	@Override
	public void onResume() {
		meal = service.getCurrentMenu().getMealByType(type);
		refreshCourse();
		super.onResume();
	}

}
