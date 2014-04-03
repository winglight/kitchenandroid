package com.syt.health.kitchen.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SettingsActivity;
import com.syt.health.kitchen.R.id;
import com.syt.health.kitchen.R.layout;
import com.syt.health.kitchen.R.string;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.CourseConditionModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.HealthConditionModel;
import com.syt.health.kitchen.db.common.TasteModel;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.service.param.AddCourseSearchParam;
import com.syt.health.kitchen.service.param.SmartMealConditionParam;
import com.syt.health.kitchen.utils.Utils;
import com.wheel.widget.WheelAdapter;
import com.wheel.widget.WheelView;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddCourseFragment extends Fragment {
	public static final String TAG = "AddCourseFragment";
	private static String MEAL_DATA_EXTRA = "meal";
	private static String MENU_DATA_EXTRA = "menu";

	private Menu menu;
	private Meal meal;
	private LinearLayout searchPanel;
	private EditText keywordTxt;
	private ListView resultList;

	private boolean isSearchPanelShowing = true;
	private boolean isNoMoreCourse = false;

	private AddCourseSearchParam params;

	private List<Course> allCourseList;
	private List<Course> selCourseList;
	private List<Course> oldCourseList; 
//	private List<Course> oldCourseList;

	private ServiceImpl service;

	public static AddCourseFragment newInstance(Menu menu, Meal meal) {
		final AddCourseFragment f = new AddCourseFragment();

		final Bundle args = new Bundle();
		args.putSerializable(MENU_DATA_EXTRA, menu);
		args.putSerializable(MEAL_DATA_EXTRA, meal);
		f.setArguments(args);

		return f;
	}

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public AddCourseFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		menu = (getArguments() != null ? (Menu) getArguments().getSerializable(
				MENU_DATA_EXTRA) : null);
		meal = (getArguments() != null ? (Meal) getArguments().getSerializable(
				MEAL_DATA_EXTRA) : null);
		allCourseList = new ArrayList<Course>();
		selCourseList = new ArrayList<Course>();
		selCourseList.addAll(Utils.convertMealCourse(meal.getItems()));
//		oldCourseList = Utils.convertMealCourse(meal.getItems());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_add_course,
				container, false);
		searchPanel = (LinearLayout) v.findViewById(R.id.addCourseSearchPanel);
		keywordTxt = (EditText) v.findViewById(R.id.addCourseKeywordTxt);
		resultList = (ListView) v.findViewById(R.id.addCourseSearchResultList);

		if (meal != null) {
		} else {
			toastMsg(R.string.meal_null);
		}

		service = ((BaseActivity) getActivity()).getService();

		initList();

		CheckBox complyChk = (CheckBox) v.findViewById(R.id.addCourseComplyChk);
		complyChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					UserModel user = service.getCurrentUser();
					if (user != null && user.getObjSmartParams() != null) {
						params.setHealthcondition(user.getObjSmartParams()
								.getHealthcondition());
					}
				}

			}
		});

		final WheelView tastWheel = (WheelView) v
				.findViewById(R.id.addCourseTastWheel);
		final List<TasteModel> tastList = service.getAllTaste();
		tastWheel.setAdapter(new WheelAdapter() {

			@Override
			public int getMaximumLength() {
				return 0;
			}

			@Override
			public int getItemsCount() {
				return tastList.size();
			}

			@Override
			public String getItem(int index) {
				return tastList.get(index).getName();
			}
		});
		final WheelView categoryWheel = (WheelView) v
				.findViewById(R.id.addCourseCategoryWheel);
		final List<CourseConditionModel> catList = service
				.getAllCourseCondition();
		categoryWheel.setAdapter(new WheelAdapter() {

			@Override
			public int getMaximumLength() {
				return 0;
			}

			@Override
			public int getItemsCount() {
				return catList.size();
			}

			@Override
			public String getItem(int index) {
				return catList.get(index).getName();
			}
		});

		final Button searchBtn = (Button) v
				.findViewById(R.id.addCourseSearchBtn);
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (params == null) {
					params = new AddCourseSearchParam();
				}
				params.setKeyword(keywordTxt.getText().toString());
				params.setTaste(tastList.get(tastWheel.getCurrentItem())
						.getId());
				params.setClassify(catList.get(categoryWheel.getCurrentItem())
						.getId());
				params.setPage(1);

				if (isSearchPanelShowing) {
					searchPanel.setVisibility(View.GONE);
					searchBtn.setText(R.string.show_conditions);

					//clear results got last time
					allCourseList.clear();
					
					searchCourse();
				} else {
					searchPanel.setVisibility(View.VISIBLE);
					searchBtn.setText(R.string.search);
				}
				isSearchPanelShowing = !isSearchPanelShowing;
			}
		});
		
		final Button finishBtn = (Button) v
				.findViewById(R.id.addCourseFinishBtn);
		finishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selCourseList.size() > 0) {
					saveNFinish();
					
				}else{
					//TODO:hint user no selection course
				}
			}
		});

		return v;
	}

	protected void searchCourse() {
		service.fetchCourseByParam(new TaskCallBack<AddCourseSearchParam, MessageModel<List<Course>>>() {

			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				if (result.isFlag()) {
					List<Course> list = result.getData();
					
					if(list.size() == 0){
						isNoMoreCourse = true;
					}
					
					allCourseList.addAll(list);
				} else {
					isNoMoreCourse = true;
					toastMsg(result.getMessage());
				}
				refreshList();
			}

			@Override
			public AddCourseSearchParam getParameters() {

				return params;
			}
		});

	}
	
	private void saveNFinish(){
		final ProgressDialog dialog = ProgressDialog.show(
				getActivity(),
				getString(R.string.dailog_wait_title),
				getString(R.string.dailog_wait_update_meal), true);
		
//		oldCourseList.addAll(selCourseList);
		meal.setItems(Utils.convertCourse(selCourseList));
		
		service.updateOrNewMeal(menu.getMenudate(), new TaskCallBack<Meal, MessageModel<Meal>>() {
			
			@Override
			public void postExecute(MessageModel<Meal> result) {
				// TODO hint user successful result
				dialog.dismiss();
				
				((BaseActivity)getActivity()).backFragment(AddCourseFragment.this);
				
			}
			
			@Override
			public Meal getParameters() {
				return meal;
			}
		});
	}

	protected void refreshList() {
		if (getActivity() == null)
			return;

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				((BaseAdapter) resultList.getAdapter()).notifyDataSetChanged();

			}
		});

	}

	private void initList() {
		resultList.setItemsCanFocus(false);
		resultList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// load data from db
		ResultListAdapter adapter = new ResultListAdapter(); 
		resultList.setAdapter(adapter);

		resultList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				Course course = allCourseList.get(position);
				if (Utils.listContains(course, selCourseList)) {
					Utils.listRemove(course, selCourseList);
					((CheckBox) v).setChecked(false);
				} else {
					Utils.listAdd(course, selCourseList);
					((CheckBox) v).setChecked(true);
				}

			}
		});
	}

	public class ResultListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return allCourseList.size()==0?0:allCourseList.size()+1;
		}

		@Override
		public Course getItem(int position) {
			return allCourseList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(allCourseList.size() > 0 && position == allCourseList.size()){
				TextView text = new TextView(getActivity());
				if(isNoMoreCourse){
					text.setText(R.string.list_no_more_items);
				}else{
					text.setText(R.string.list_loading_more);
					
					params.setPage(params.getPage() + 1);
					
					searchCourse();
				}
				
				return text;
			}else{
			
			Course course = allCourseList.get(position);
			CheckBox textView = new CheckBox(getActivity());
			textView.setPadding(100, 0, 0, 0);
			textView.setText(course.getName());
			textView.setTextColor(getActivity().getResources().getColor(android.R.color.black));
			textView.setFocusable(false);
			textView.setFocusableInTouchMode(false);
			textView.setClickable(false);
			// textView.setOnCheckedChangeListener(this);
			// textView.setTag(condition.getId());
			boolean flag = Utils.listContains(course, selCourseList);
			textView.setChecked(flag);
			return textView;
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void toastMsg(int resId) {
		final String msg = this.getString(resId);
		toastMsg(msg);
	}

	public void toastMsg(final String msg) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}
}
