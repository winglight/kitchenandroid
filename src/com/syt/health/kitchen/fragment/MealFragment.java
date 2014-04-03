package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SetFruitActivity;
import com.syt.health.kitchen.SetMainFoodActivity;
import com.syt.health.kitchen.fragment.NoteFragment.ClickListenerForScrolling;
import com.syt.health.kitchen.fragment.NoteFragment.SizeCallbackForMenu;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.ShakeListener;
import com.syt.health.kitchen.utils.Utils;
import com.syt.health.kitchen.widget.ActionItem;
import com.syt.health.kitchen.widget.MyHorizontalScrollView;
import com.syt.health.kitchen.widget.QuickAction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MealFragment extends Fragment implements RefreshableFragment {

	public static final String course_history_file = "search_course_file";
	public static final String LOG_TAG = "search_course_file";
	public static final String TAG = "MEALFRAGMENT";

	private Button edit_btn;
	private boolean readOnly = true;
	private ListView course_lv;

	private Meal meal;
	private String date;
	private ListViewAdapter adapter;
	private ServiceImpl service;
	private GenerateCondition conditionParam;
	private View footer_view;
	private MyHorizontalScrollView summarize_scroll;
	private Button mainFood_btn;
	private int type;
	private TextView totalFoodCalorie_tv;// 选择物的总热量
	private TextView breakfastComments_tv;
	private TextView comments_tv;
	private View recommend_fruit;
	private LinearLayout comments_layout;

	private ShakeListener mShaker;
	private MediaPlayer mp;
    private TextView recFoodCalorie_tv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		service = ((BaseActivity) getActivity()).getService();

		type = getArguments().getInt(Utils.MEAL_TYPE_KEY);
		Menu menu = service.getCurrentMenu();
		date = menu.getMenudate();
		conditionParam = menu.getSmartParams();
		meal = menu.getMealByType(type);
		if (meal == null) {
			meal = new Meal();
		}

		// final Vibrator vibe =
		// (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

		mShaker = new ShakeListener(getActivity());
		mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
			public void onShake() {
				if (((NoteActivity) getActivity()).isReadOnly())
					return;
				// vibe.vibrate(100);
				try{
					mp.start();
				}catch(RuntimeException re){
				}

				mShaker.pause();
				generateMeal();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mealfragment, container,
				false);
		init(view);
		switch (type) {
		case Utils.BREAKFAST:
			summarize_scroll.setVisibility(View.GONE);
			comments_layout.setVisibility(View.VISIBLE);
			break;
		case Utils.LUNCH:
			summarize_scroll.setVisibility(View.VISIBLE);
			comments_layout.setVisibility(View.GONE);
			mainFood_btn.setOnClickListener(new ClickListenerForScrolling(
					summarize_scroll, recommend_fruit, mainFood_btn));
			mainFood_btn.setBackgroundResource(R.drawable.mainfood_bg_left);
			break;
		case Utils.DINNER:
			summarize_scroll.setVisibility(View.VISIBLE);
			comments_layout.setVisibility(View.GONE);
			mainFood_btn.setOnClickListener(new ClickListenerForScrolling(
					summarize_scroll, recommend_fruit, mainFood_btn));
			mainFood_btn.setBackgroundResource(R.drawable.mainfood_bg_left);
			break;
		}
		((NoteActivity) getActivity()).setStep(0);
		return view;
	}

	@Override
	public void onResume() {
		Menu menu = service.getCurrentMenu();

		refreshData(menu);

		mShaker.resume();

		mp = MediaPlayer.create(getActivity(), R.raw.shake);

		super.onResume();
	}

	@Override
	public void onPause() {
		mShaker.pause();
		mp.release();

		super.onPause();
	}

	@Override
	public void onStop() {

		super.onStop();
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void init(View view) {
		comments_layout = (LinearLayout) view
				.findViewById(R.id.fragment_mealfragment_breakfast_health_comments_layout);
		breakfastComments_tv = (TextView) view
				.findViewById(R.id.fragment_mealfragment_health_comments_tv);

		footer_view = LayoutInflater.from(getActivity()).inflate(
				R.layout.footer, null);

		final AutoCompleteTextView auto_tv = (AutoCompleteTextView) view
				.findViewById(R.id.fragment_mealfragment_search_auto);
		Utils.initAutoComplete(getActivity(), NoteActivity.HISTORY, auto_tv,
				course_history_file);
		Button search_btn = (Button) view
				.findViewById(R.id.fragment_mealfragment_search_btn);
		if (!((NoteActivity) getActivity()).isReadOnly()) {
			search_btn.setEnabled(true);
		} else {
			search_btn.setEnabled(false);
		}
		search_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (auto_tv.getText().toString().equals("")) {
					Toast.makeText(
							getActivity(),
							getActivity().getResources().getString(
									R.string.keywords), Toast.LENGTH_LONG)
							.show();
				} else {
					Utils.inputMethodManagerDismiss(getActivity(), auto_tv);
					Utils.saveAutoHistory(getActivity(), NoteActivity.HISTORY,
							auto_tv, course_history_file);
					KeySearchCourseFragment mFragment = KeySearchCourseFragment
							.newInstance(type, auto_tv.getText().toString());
					((BaseActivity) getActivity()).addFragment(mFragment,
							KeySearchCourseFragment.TAG,
							R.id.activity_main_left_linear);
				}
			}
		});

		Button addCourse_btn = (Button) view
				.findViewById(R.id.fragment_mealfragment_addcourse_btn);

		if (!((NoteActivity) getActivity()).isReadOnly()) {
			addCourse_btn.setEnabled(true);
		} else {
			addCourse_btn.setEnabled(false);
		}
		addCourse_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CourseAddFragment mFragment = CourseAddFragment
						.newInstance(type);
				if (type == Utils.BREAKFAST) {
					((BaseActivity) getActivity()).addFragment(mFragment,
							CourseAddFragment.TAG_MAIN_FOOD,
							R.id.activity_main_left_linear);
				} else {
					((BaseActivity) getActivity()).addFragment(mFragment,
							CourseAddFragment.TAG_ADD,
							R.id.activity_main_left_linear);
				}
			}
		});

		course_lv = (ListView) view
				.findViewById(R.id.fragment_mealfragment_course_lv);
		course_lv.setDivider(null);
		adapter = new ListViewAdapter();
		if (meal.getItems().size() != 0) {
			course_lv.addFooterView(footer_view);
		}
		course_lv.setAdapter(adapter);
		course_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 < meal.getItems().size()) {
					CourseInfoFragment mFragment = CourseInfoFragment
							.newInstance((ArrayList<Course>) Utils
									.convertMealCourse(meal.getItems()), arg2);
					((BaseActivity) getActivity()).addFragment(mFragment,
							CourseInfoFragment.TAG,
							R.id.activity_main_left_linear);
				}

			}
		});
		Button recommend_btn = (Button) view
				.findViewById(R.id.fragment_mealfragment_recommend_btn);
		if (!((NoteActivity) getActivity()).isReadOnly()) {
			recommend_btn.setEnabled(true);
		} else {
			recommend_btn.setEnabled(false);
		}
		recommend_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (getPrefShake()) {
					new AlertDialog.Builder(getActivity())
							.setMessage(R.string.shake_tips)
							.setPositiveButton(
									getResources().getString(
											R.string.know_dialog),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											savePrefShake(false);
											mShaker.pause();
											generateMeal();
										}
									}).show();
				} else {
					generateMeal();
				}

				// new AlertDialog.Builder(getActivity())
				// .setMessage(
				// getResources().getString(R.string.refresh_meal))
				// .setPositiveButton(
				// getResources().getString(R.string.yes),
				// new DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {

				// }
				// })
				// .setNegativeButton(
				// getResources().getString(R.string.cancel), null)
				// .show();
			}
		});

		edit_btn = (Button) view
				.findViewById(R.id.fragment_mealfragment_eidt_btn);
		edit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (readOnly) {
					edit_btn.setText(getString(R.string.condition_done));
					edit_btn.setTextColor(Color.RED);
					edit_btn.setBackgroundResource(R.drawable.meal_finish_action_bg);
					readOnly = false;
				} else {
					edit_btn.setText(getString(R.string.delete_course));
					edit_btn.setTextColor(Color.BLACK);
					edit_btn.setBackgroundResource(R.drawable.meal_action_state);
					readOnly = true;
				}
				adapter.notifyDataSetChanged();
			}
		});

		summarize_scroll = (MyHorizontalScrollView) view
				.findViewById(R.id.fragment_meal_summarizescrollview);

		View health_value = LayoutInflater.from(getActivity()).inflate(
				R.layout.meal_health_value, null);
		recommend_fruit = LayoutInflater.from(getActivity()).inflate(
				R.layout.meal_recommend_value, null);
		mainFood_btn = (Button) recommend_fruit
				.findViewById(R.id.notefragment_recommend_fruit_btn);
		mainFood_btn.setBackgroundResource(R.drawable.mainfood_bg_right);
		Button health_btn = (Button) health_value
				.findViewById(R.id.health_comments_btn);
		comments_tv = (TextView) health_value
				.findViewById(R.id.health_comments);
		health_btn.setBackgroundResource(R.drawable.meal_reviewbtn_active);

		recFoodCalorie_tv = (TextView) recommend_fruit
				.findViewById(R.id.notefragment_recommend_fruit_calorie_tv);
		recFoodCalorie_tv.setText(meal.getAdvicedCals(conditionParam
				.getPeople()));
		totalFoodCalorie_tv = (TextView) recommend_fruit
				.findViewById(R.id.notefragment_total_fruit_calorie_tv);

		final View[] children = { health_value, recommend_fruit };
		int scrollToViewIdx = 1;
		summarize_scroll.initViews(children, scrollToViewIdx,
				new SizeCallbackForMenu(mainFood_btn));
		Button addFruit_btn = (Button) recommend_fruit
				.findViewById(R.id.notefragment_recommend_fruit_add_btn);
		addFruit_btn.setVisibility(View.VISIBLE);
		if (!((NoteActivity) getActivity()).isReadOnly()) {
			addFruit_btn.setEnabled(true);
		} else {
			addFruit_btn.setEnabled(false);
		}
		addFruit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				((BaseActivity) getActivity()).addFragment(
						CourseAddFragment.newInstance(type),
						CourseAddFragment.TAG_MAIN_FOOD,
						R.id.activity_main_left_linear);

			}
		});
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mainFood_btn.performClick();
			}
		}, 100);
	}

	/**
	 * 绑定fragment
	 * 
	 * @param date
	 * @param meal
	 * @return
	 */
	public static MealFragment newInstance(String date, int type) {
		MealFragment fragment = new MealFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Utils.DATE_KEY, date);
		bundle.putInt(Utils.MEAL_TYPE_KEY, type);
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * 重写listview适配器
	 * 
	 * @author ChengFaner
	 * 
	 */
	private class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return meal.getItems().size();
		}

		@Override
		public Object getItem(int position) {

			return meal.getItems().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Holder h;
			if (convertView == null) {

				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.mealfragment_course_lv_item, null);
				h = new Holder(convertView);
				convertView.setTag(h);
			} else {
				h = (Holder) convertView.getTag();
			}
			if (position == 0) {
				h.header_layout.setVisibility(View.VISIBLE);
			} else {
				h.header_layout.setVisibility(View.GONE);
			}
			final MealCourse mealCourse = meal.getItems().get(position);
			if (meal.getItems().size() <= 9) {
				if (position == meal.getItems().size() - 1) {
					h.middle_view.setVisibility(View.GONE);
				} else {
					h.middle_view.setVisibility(View.VISIBLE);
				}
			}
			String url = mealCourse.getCourse().getListPicUrl();
			((BaseActivity) getActivity()).getmImageFetcher().loadImage(url,
					h.course_iv);

			h.courseName_btn.setText(mealCourse.getCourse().getName());
			h.courseInfo_tv.setText(mealCourse.getCourse().getCoursecond()
					+ "   "
					+ new Double(mealCourse.getCourse().getCalories())
							.intValue() + getString(R.string.caluli));

			// handle searching yiji
			String tmpHealth = (getActivity() instanceof NoteActivity) ? ((NoteActivity) getActivity())
					.getCurrentHealthCondition() : null;
			if (tmpHealth != null) {
				if (mealCourse.getCourse().getEffectivity() != null
						&& mealCourse.getCourse().getEffectivity().size() > 0) {
					h.good_iv
							.setBackgroundResource(R.drawable.good_button_state);
					h.good_iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							QuickAction mAction = new QuickAction(getActivity());
							ActionItem item = new ActionItem(0, Utils
									.arrayIntoString(mealCourse.getCourse()
											.getEffectivity()));
							mAction.addActionItem(item);
							mAction.show(v);
						}
					});
				} else {
					h.good_iv.setBackgroundResource(R.drawable.good_no_bg);
					h.good_iv.setOnClickListener(null);
				}

				if (mealCourse.getCourse().getIncompatible() != null
						&& mealCourse.getCourse().getIncompatible().size() > 0) {
					h.bad_iv.setBackgroundResource(R.drawable.bad_button_state);
					h.bad_iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							QuickAction mAction = new QuickAction(getActivity());
							ActionItem item = new ActionItem(0, Utils
									.arrayIntoString(mealCourse.getCourse()
											.getIncompatible()));
							mAction.addActionItem(item);
							mAction.show(v);
						}
					});
				} else {
					h.bad_iv.setBackgroundResource(R.drawable.bad_no_bg);
					h.bad_iv.setOnClickListener(null);
				}
			} else {
				if (mealCourse.getCourse()
						.getGoodDesc(conditionParam.getHealthcondition())
						.size() != 0) {
					h.good_iv
							.setBackgroundResource(R.drawable.good_button_state);
					h.good_iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							QuickAction mAction = new QuickAction(getActivity());
							ActionItem item = new ActionItem(
									0,
									Utils.arrayIntoString(mealCourse
											.getCourse()
											.getGoodDesc(
													conditionParam
															.getHealthcondition())));
							mAction.addActionItem(item);
							mAction.show(v);
						}
					});
				} else {
					h.good_iv.setBackgroundResource(R.drawable.good_no_bg);
					h.good_iv.setOnClickListener(null);
				}

				if (mealCourse.getCourse()
						.getBadDesc(conditionParam.getHealthcondition()).size() != 0) {
					h.bad_iv.setBackgroundResource(R.drawable.bad_button_state);
					h.bad_iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							QuickAction mAction = new QuickAction(getActivity());
							ActionItem item = new ActionItem(
									0,
									Utils.arrayIntoString(mealCourse
											.getCourse()
											.getBadDesc(
													conditionParam
															.getHealthcondition())));
							mAction.addActionItem(item);
							mAction.show(v);
						}
					});
				} else {
					h.bad_iv.setBackgroundResource(R.drawable.bad_no_bg);
					h.bad_iv.setOnClickListener(null);
				}
			}

			String cond = mealCourse.getCourse().getCoursecond();

			if (cond.equals(Utils.COURSE_CONDITION_ZAODIAN)
					|| cond.equals(Utils.COURSE_CONDITION_ZHUSHI)) {
				h.modify_btn.setVisibility(View.VISIBLE);
				h.modify_btn.setText(mealCourse.getQuantity() + "份");
				if (!((NoteActivity) getActivity()).isReadOnly()) {
					h.modify_btn.setEnabled(true);
					h.modify_btn.setTextColor(Color.BLACK);
					h.modify_btn
							.setBackgroundResource(R.drawable.modify_course_button_state);
				} else {
					h.modify_btn.setTextColor(Color.GRAY);
					h.modify_btn
							.setBackgroundResource(R.drawable.modify_course_bg_press);
					h.modify_btn.setEnabled(false);
				}
				h.modify_btn
						.setBackgroundResource(R.drawable.modify_course_button_state);
				h.modify_btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(),
								SetMainFoodActivity.class);
						intent.putExtra(Utils.DATE_KEY, date);
						intent.putExtra(Utils.MEAL_TYPE_KEY, type);
						intent.putExtra(Utils.MEALCOURSE, mealCourse);
						startActivity(intent);
					}
				});
			} else {
				h.modify_btn.setVisibility(View.GONE);
			}

			if (readOnly) {
				h.delete_iv.setVisibility(View.GONE);
			} else {
				h.delete_iv.setVisibility(View.VISIBLE);
				h.delete_iv.setBackgroundResource(R.drawable.search_btn_delete);
				h.delete_iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteCourse(mealCourse);
					}
				});
			}
			return convertView;
		}
	}

	class Holder {
		LinearLayout header_layout;
		ImageView course_iv;
		TextView courseName_btn;
		ImageView good_iv;
		ImageView bad_iv;
		Button delete_iv;
		TextView courseInfo_tv;
		View middle_view;
		TextView modify_btn;

		public Holder(View view) {
			course_iv = (ImageView) view
					.findViewById(R.id.mealfragment_course_lv_course_photo_iv);
			courseName_btn = (TextView) view
					.findViewById(R.id.mealfragment_course_lv_course_name_btn);
			good_iv = (ImageView) view
					.findViewById(R.id.mealfragment_course_lv_good_iv);
			bad_iv = (ImageView) view
					.findViewById(R.id.mealfragment_course_lv_bad_iv);
			courseInfo_tv = (TextView) view
					.findViewById(R.id.mealfragment_course_lv_course_info_tv);
			delete_iv = (Button) view
					.findViewById(R.id.mealfragment_course_lv_delete_iv);
			middle_view = (View) view
					.findViewById(R.id.mealfragment_course_lv_middle_view);
			modify_btn = (TextView) view
					.findViewById(R.id.mealfragment_course_lv_modify_btn);
			header_layout = (LinearLayout) view
					.findViewById(R.id.mealfragment_course_lv_header_layout);
		}
	}

	private void generateMeal() {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				"正在生成新的餐次...");
		service.generateMealByHealthCondition(service.getCurrentMenu(),
				new TaskCallBack<Meal, MessageModel<Meal>>() {
					@Override
					public void postExecute(MessageModel<Meal> result) {

						if (result.isFlag()) {
							if (result.getData().getItems().size() != 0) {
								if (course_lv.getFooterViewsCount() == 0) {
									course_lv.addFooterView(footer_view);
									edit_btn.setTextColor(Color.BLACK);
								}
							}
							Menu menu = service.getCurrentMenu();
							refreshData(menu);

						}
						pd.dismiss();

						mShaker.resume();
					}

					@Override
					public Meal getParameters() {
						return meal;
					}
				});
	}

	private void deleteCourse(MealCourse mealCourse) {
		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
				"正在删除菜品");
		service.removeCourseFromMeal(date, mealCourse,
				new TaskCallBack<Meal, MessageModel<Meal>>() {
					@Override
					public void postExecute(MessageModel<Meal> result) {
						if (result.isFlag()) {
							if (result.getData().getItems().size() == 0) {
								readOnly = true;
								edit_btn.setText(getString(R.string.delete_course));
								edit_btn.setTextColor(Color.BLACK);
								edit_btn.setBackgroundResource(R.drawable.meal_action_state);
								course_lv.removeFooterView(footer_view);
							}
							Menu menu = service.getCurrentMenu();
							refreshData(menu);
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

	class ClickListenerForScrolling implements View.OnClickListener {
		HorizontalScrollView scrollView;
		View view;
		Button btn;
		/**
		 * Menu must NOT be out/shown to start with.
		 */
		boolean menuOut = false;

		public ClickListenerForScrolling(HorizontalScrollView scrollView,
				View view, Button btn) {
			super();
			this.scrollView = scrollView;
			this.view = view;
			this.btn = btn;
		}

		@Override
		public void onClick(View v) {

			int menuWidth = view.getMeasuredWidth();

			// Ensure menu is visible
			view.setVisibility(View.VISIBLE);

			if (!menuOut) {
				// Scroll to 0 to reveal menu
				int left = 0;
				scrollView.smoothScrollTo(left, 0);
				btn.setBackgroundResource(R.drawable.mainfood_bg_right);

			} else {
				// Scroll to menuWidth so menu isn't on screen.
				int left = menuWidth;
				scrollView.smoothScrollTo(left, 0);
				btn.setBackgroundResource(R.drawable.mainfood_bg_left);
			}
			menuOut = !menuOut;
		}
	}

	public void refreshData(Menu menu) {
		if (getActivity() == null)
			return;

		((NoteActivity) getActivity()).setCurrentHealthCondition(null);
		((NoteActivity) getActivity()).setSearchBadCondition(false);

		meal = menu.getMealByType(type);
		if (!((NoteActivity) getActivity()).isReadOnly()
				&& meal.getItems().size() != 0) {
			edit_btn.setEnabled(true);
		} else {
			edit_btn.setEnabled(false);
		}
		totalFoodCalorie_tv.setText(meal.getTotalCals(Utils.filterCourse(meal),
				false));
		breakfastComments_tv.setText(Html.fromHtml(meal
				.getComments(conditionParam)));
		comments_tv.setText(Html.fromHtml(meal.getComments(conditionParam)));
		comments_tv.invalidate();
		breakfastComments_tv.invalidate();
		recFoodCalorie_tv.setText(meal.getAdvicedCals(conditionParam
				.getPeople()));
		adapter.notifyDataSetChanged();
	}

	private void savePrefShake(boolean flag) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		Editor editor = prefs.edit();
		editor.putBoolean("shake_tips", flag);
		editor.commit();
	}

	private boolean getPrefShake() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		return prefs.getBoolean("shake_tips", true);

	}
}
