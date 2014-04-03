package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.List;
import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.FoodInfoActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SetFruitActivity;
import com.syt.health.kitchen.SetMainFoodActivity;
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

public class KeySearchCourseFragment extends Fragment {

	public static final String TAG = "KeySearchCourseFragment";
	public static final String KEY_WORD = "KEY_WORD";
	
	private AutoCompleteTextView auto_tv;
	private ServiceImpl service;

	private List<Course> courses = new ArrayList<Course>();
	private ListView course_lv;
	private CourseListAdapter adapter;
	private Meal meal;
	private String date;
	private View moreView;
	private Button load_btn;
	private ProgressBar load_pb;
	private GenerateCondition conditionParam;

	private List<Course> exist_course = new ArrayList<Course>();
	private int page = 1;
	private View footer_view;
	private int type;
	
	private String keyword;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		service = ((BaseActivity) getActivity()).getService();
		type = (getArguments() != null) ? getArguments().getInt(
				Utils.MEAL_TYPE_KEY) : 0;
				meal = service.getCurrentMenu().getMealByType(type);
		date = service.getCurrentMenu().getMenudate();
		conditionParam = service.getCurrentUser().getObjSmartParams();
		
		keyword = (getArguments() != null) ? getArguments().getString(KEY_WORD) : "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_keysearch_course,
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
		footer_view = LayoutInflater.from(getActivity()).inflate(
				R.layout.footer, null);
		moreView = LayoutInflater.from(getActivity()).inflate(
				R.layout.moredata, null);
		load_pb = (ProgressBar) moreView.findViewById(R.id.moredata_pb);
		load_btn = (Button) moreView.findViewById(R.id.moredata_load_btn);
		load_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				load_btn.setVisibility(View.GONE);
				load_pb.setVisibility(View.VISIBLE);

					page++;
					searchCourse();
			}
		});

		course_lv = (ListView) view
				.findViewById(R.id.fragment_keysearch_course_lv);
		course_lv.setDivider(null);
		course_lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Course course = courses.get(position);
				if(Utils.COURSE_CONDITION_FRUIT.equals(course.getCoursecond())){
					Intent intent = new Intent();
					intent.setClass(getActivity(), FoodInfoActivity.class);
					intent.putExtra(FoodInfoActivity.FOOD_KEY, course.getItems().get(0).getFood());
					startActivity(intent);
				}else{
				((BaseActivity) getActivity()).addFragment(CourseInfoFragment
						.newInstance(course),
						CourseInfoFragment.TAG, R.id.activity_main_left_linear);
				}
			}
		});

			auto_tv = (AutoCompleteTextView) view
					.findViewById(R.id.fragment_keysearch_search_auto);
			auto_tv.setText(keyword);
			Utils.initAutoComplete(getActivity(), NoteActivity.HISTORY,
					auto_tv, MealFragment.course_history_file);
			// auto_tv.setOnFocusChangeListener(new View.OnFocusChangeListener()
			// {
			//
			// @Override
			// public void onFocusChange(View v, boolean hasFocus) {
			// Utils.inputMethodManagerDismiss(getActivity(), auto_tv);
			// }
			// });

			Button search_btn = (Button) view
					.findViewById(R.id.fragment_keysearch_search_btn);
			search_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					page = 1;
					if (auto_tv.getText().toString().equals("")) {
						Toast.makeText(
								getActivity(),
								getActivity().getResources().getString(
										R.string.keywords), Toast.LENGTH_LONG)
								.show();
					} else {
						courses.clear();
						Utils.inputMethodManagerDismiss(getActivity(), auto_tv);
						Utils.saveAutoHistory(getActivity(),
								NoteActivity.HISTORY, auto_tv,
								MealFragment.course_history_file);
						searchCourse();
					}
				}
			});

			searchCourse();
	}


	/**
	 * 绑定fargment
	 * 
	 * @param meal
	 * @param date
	 * @return
	 */
	public static KeySearchCourseFragment newInstance(int meal_type,
			String key) {
		KeySearchCourseFragment fragment = new KeySearchCourseFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Utils.MEAL_TYPE_KEY, meal_type);
		bundle.putString(KEY_WORD, key);
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

				final List<String> goodDesc = course
						.getGoodDesc(conditionParam.getHealthcondition());
				if (goodDesc.size() != 0) {
					h.good_iv
							.setBackgroundResource(R.drawable.good_button_state);
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

				final List<String> badDesc = course.getBadDesc(conditionParam.getHealthcondition());
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
									|| Utils.COURSE_CONDITION_ZAODIAN
											.equals(course.getCoursecond())) {
								Intent intent = new Intent(getActivity(),
										SetMainFoodActivity.class);
								intent.putExtra(Utils.DATE_KEY, date);
								intent.putExtra(Utils.MEAL_TYPE_KEY, type);
								MealCourse mealCourse = new MealCourse();
								mealCourse.setCourse(course);
								mealCourse.setQuantity(0);
								mealCourse.setUnit(course.getUnit());
								intent.putExtra(Utils.MEALCOURSE, mealCourse);
								startActivity(intent);
							}else if (Utils.COURSE_CONDITION_FRUIT.equals(course
									.getCoursecond())) {
								Intent intent = new Intent(getActivity(),
										SetFruitActivity.class);
								intent.putExtra(Utils.DATE_KEY, date);
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
					+ course.getCalories() +getString(R.string.caluli));
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


	/**
	 * 根据关键字搜索菜品
	 */
	private void searchCourse() {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				getString(R.string.search_course));
		service.queryCourseByLikeWords(page, false,
				new TaskCallBack<String, MessageModel<List<Course>>>() {
					@Override
					public void postExecute(MessageModel<List<Course>> result) {
						List<Course> add_course = new ArrayList<Course>();
						course_lv.removeFooterView(moreView);
						course_lv.removeFooterView(footer_view);
						if (result.isFlag()) {
							add_course = result.getData();

							if (add_course == null) {
								Toast.makeText(getActivity(),
										result.getMessage(), Toast.LENGTH_LONG)
										.show();
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
					public String getParameters() {
						return auto_tv.getText().toString();
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
		service.addCourseToMeal(date, mc,
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
