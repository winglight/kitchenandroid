package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.BuyingActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.DateUtils;
import com.syt.health.kitchen.utils.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class BuyEditorFragment extends Fragment {  
	private String date;
	private TextView date_tv;
	private Button up_btn;
	private Button next_btn;
	private Button save_btn;
	private ServiceImpl service;
	private ExpandableListAdapter adapter;
	private List<String> group = new ArrayList<String>();
	private LayoutInflater inflater;
	private Map<String, List<List<CourseFood>>> allMap = new HashMap<String, List<List<CourseFood>>>();// 对应日期的所有菜
	private Map<String, Map<String, List<CourseFood>>> selectedMaps = new HashMap<String, Map<String, List<CourseFood>>>();// 对应日期选中的菜
	private List<CourseFood> total_courseFoods = new ArrayList<CourseFood>();// 所有选中的食材
	private final static String KEY_LUNCH = "中餐";
	private final static String KEY_DINNER = "晚餐";
	public final static String TAG = "BuyEditorFragment";
	private int currIndex = 0;
	private List<Menu> menus = new ArrayList<Menu>();
	private ExpandableListView expandableListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		service = ((BaseActivity) getActivity()).getService();
		inflater = LayoutInflater.from(getActivity());
		date = DateUtils.defaultFormat(new Date());
		menus = service.getMenuIn7Days();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		((BuyingActivity) getActivity()).setExit(false);

		View view = inflater.inflate(R.layout.fragment_buyingchoice, container,
				false);
		init(view);
		if (menus.size() > 0) {
			getCourses(menus.get(currIndex));
		} else {
			Toast.makeText(getActivity(),
					getResources().getString(R.string.buyinglist_no_menu),
					Toast.LENGTH_LONG).show();
		}
		return view;
	}

	private void init(View view) {
		expandableListView = (ExpandableListView) view
				.findViewById(R.id.fragment_buyingchoice_course_expandablelistView);
		// expandableListView.setOnChildClickListener(expandableListView_listener);

		date_tv = (TextView) view
				.findViewById(R.id.fragment_buyingchoice_date_tv);
		date_tv.setText(date);
		up_btn = (Button) view.findViewById(R.id.fragment_buyingchoice_up_btn);
		save_btn = (Button) view
				.findViewById(R.id.fragment_buyingchoice_save_btn);
		save_btn.setOnClickListener(save_btn_listener);
		up_btn.setOnClickListener(up_btn_listener);
		next_btn = (Button) view
				.findViewById(R.id.fragment_buyingchoice_next_btn);
		next_btn.setOnClickListener(next_btn_listener);
		// addView();

	}

	/**
	 * 获取viewpager的item个数
	 */
	// private void addView(){
	// List<Menu> menus = service.getMenuIn7Days();
	// for (int j = 0; j < 7; j++) {
	// if(j<menus.size()){
	// if(menus.get(j)!=null){
	// View view =
	// LayoutInflater.from(getActivity()).inflate(R.layout.expandablelistview,
	// null);
	// ExpandableListView expandableListView =
	// (ExpandableListView)view.findViewById(R.id.expandablelistview);
	// expandableListView.setOnChildClickListener(expandableListView_listener);
	// getCourses(menus.get(j));
	// adapter = new ExpandableListAdapter(menus.get(j).getMenudate());
	// expandableListView.setAdapter(adapter);
	// mViews.add(view);
	// }
	// }else{
	// TextView tv = new TextView(getActivity());
	// tv.setText("您还未设定当前日期的餐次");
	// mViews.add(tv);
	// }
	// }
	// }
	/**
	 * 初始化数据
	 * 
	 * @param menu
	 */
	// private void initializeData(Menu menu){
	// if(menu!=null){
	// lunchMeal = menu.getLunch();
	// dinnerMeal = menu.getDinner();
	// group.clear();
	// List<List<CourseFood>> courseFood = new ArrayList<List<CourseFood>>();
	// List<CourseFood> selectedCourseFood = new ArrayList<CourseFood>();
	// if(lunchMeal!=null){
	// group.add(KEY1);
	// //Map<String, List<CourseFood>> meal_map = new HashMap<String,
	// List<CourseFood>>();
	// getCourses(lunchMeal);
	// // meal_map.put(menu.getMenudate(), getChildItem(new_courses));
	// List<CourseFood> lists = getChildItem(new_courses);
	// for (CourseFood courseFood2 : lists) {
	// selectedCourseFood.add(courseFood2);
	// }
	// courseFood.add(lists);
	// }else{
	// Toast.makeText(getActivity(),getResources().getString(R.string.no_lunch),Toast.LENGTH_LONG).show();
	// }
	// if(dinnerMeal!=null){
	// group.add(KEY2);
	// getCourses(dinnerMeal);
	// List<CourseFood> lists = getChildItem(new_courses);
	// for (CourseFood courseFood2 : lists) {
	// selectedCourseFood.add(courseFood2);
	// }
	// courseFood.add(lists);
	// }else{
	// Toast.makeText(getActivity(),getResources().getString(R.string.no_dinner),Toast.LENGTH_LONG).show();
	// }
	// selectedMaps.put(menu.getMenudate(), selectedCourseFood);
	// allMap.put(menu.getMenudate(), courseFood);
	// }
	// }
	/**
	 * 保存按钮监听事件
	 */
	private OnClickListener save_btn_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			total_courseFoods.clear();

			String foodListDesc = "";

			for (Menu menu : menus) {
				String date = menu.getMenudate();
				if (selectedMaps.get(date) != null) {
					Map<String, List<CourseFood>> total = selectedMaps
							.get(date);
					List<CourseFood> courseFoods = total.get(KEY_LUNCH);
					if (courseFoods != null && courseFoods.size() > 0) {
						foodListDesc += DateUtils.getMonthDay(date, false)
								+ ": " + KEY_LUNCH;
						Utils.mergeCourseFood(courseFoods, total_courseFoods);
					}
					courseFoods = total.get(KEY_DINNER);
					if (courseFoods != null && courseFoods.size() > 0) {
						Utils.mergeCourseFood(courseFoods, total_courseFoods);
						if (foodListDesc.contains(DateUtils.getMonthDay(date,
								false))) {
							foodListDesc += " " + KEY_DINNER;
						} else {
							foodListDesc += DateUtils.getMonthDay(date, false)
									+ ": " + KEY_DINNER;
						}
					}
					if (!foodListDesc.endsWith("\n")) {
						foodListDesc += "\n";
					}

				}
			}
			if (foodListDesc.startsWith("\n")) {
				foodListDesc = foodListDesc.replaceFirst("\n", "");
			}
			if (total_courseFoods != null && total_courseFoods.size() > 0) {
				service.saveFoodList(getTotalCourseFood(total_courseFoods),
						foodListDesc);
				((BaseActivity) getActivity()).addFragment(
						BuyingListFragment.newInstance(true),
						BuyingListFragment.TAG, android.R.id.content);
			} else {
				new AlertDialog.Builder(getActivity()).
				setMessage(getString(R.string.no_food)).
				setPositiveButton(getResources().getString(R.string.know_dialog), new DialogInterface.OnClickListener() {			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
			}
		}
	};

	private List<CourseFood> getTotalCourseFood(List<CourseFood> courseFoods) {
		Map<String, CourseFood> maps = new HashMap<String, CourseFood>();
		for (CourseFood courseFood : courseFoods) {
			CourseFood newCourseFood = maps.get(courseFood.getFood().getId());
			if (newCourseFood == null)
				maps.put(courseFood.getFood().getId(), courseFood);
			else {
				newCourseFood.setQuantity(newCourseFood.getQuantity()
						+ courseFood.getQuantity());
				maps.put(courseFood.getFood().getId(), newCourseFood);
			}
		}
		return new ArrayList<CourseFood>(maps.values());
	}

	private OnClickListener up_btn_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (currIndex > 0) {
				currIndex--;
				date_tv.setText(menus.get(currIndex).getMenudate());
				getCourses(menus.get(currIndex));
			} else {
				Toast.makeText(getActivity(), R.string.no_menu_data,
						Toast.LENGTH_LONG).show();
			}
		}
	};
	private OnClickListener next_btn_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (currIndex < menus.size() - 1) {
				currIndex++;
				date_tv.setText(menus.get(currIndex).getMenudate());
				getCourses(menus.get(currIndex));
			} else {
				Toast.makeText(getActivity(), R.string.no_menu_data,
						Toast.LENGTH_LONG).show();
			}
		}
	};

	/**
	 * 重写ExpandableListAdapter
	 * 
	 * @author ChengFaner
	 * 
	 */
	class ExpandableListAdapter extends BaseExpandableListAdapter {
		private String date_key;

		public ExpandableListAdapter(String date_key) {
			this.date_key = date_key;
			Map<String, List<CourseFood>> map = selectedMaps.get(date_key);
			if (map == null) {
				map = new HashMap<String, List<CourseFood>>();
				map.put(KEY_LUNCH, new ArrayList<CourseFood>());
				map.put(KEY_DINNER, new ArrayList<CourseFood>());
				selectedMaps.put(date_key, map);

			}
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return allMap.get(date_key).get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final Holder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listview_item, null);
				holder = new Holder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			CourseFood courseFood = (CourseFood) getChild(groupPosition,
					childPosition);
			holder.tv.setText(courseFood.getQuantity()
					+ courseFood.getFoodunit());
			holder.cb.setTag(courseFood);
			holder.cb.setText(courseFood.getFood().getName());
			holder.cb.setTextColor(getResources().getColor(
					android.R.color.black));
			final List<CourseFood> list = selectedMaps.get(date_key).get(
					groupPosition == 0 ? KEY_LUNCH : KEY_DINNER);
			boolean flag = Utils.listContains(courseFood, list);
			holder.cb.setChecked(flag);

			holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					CourseFood courseFood = (CourseFood) (holder.cb.getTag());

					if (isChecked) {
						Utils.listAdd(courseFood, list);
					} else {
						Utils.listRemove(courseFood, list);
					}
				}
			});

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (groupPosition < 2) {
				return allMap.get(date_key).get(groupPosition).size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getGroup(int groupPosition) {
			return group.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return group.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.textview1, null);
			tv.setText(group.get(groupPosition));
			return tv;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	class Holder {
		CheckBox cb;
		TextView tv;

		Holder(View view) {
			cb = (CheckBox) view.findViewById(R.id.listview_item_cb);
			tv = (TextView) view
					.findViewById(R.id.listview_item_courseweight_tv);
		}
	}

	private void getCourses(final Menu menu) {
//		final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
//				getResources().getString(R.string.login));
//		
//		pd.show();
		final List<Course> lunch_course = Utils.convertMealCourse(menu
				.getLunch().getItems());
		final List<Course> dinner_course = Utils.convertMealCourse(menu
				.getDinner().getItems());

		// get lunch courses
		group.clear();
		List<List<CourseFood>> courseFood = new ArrayList<List<CourseFood>>();
		group.add(KEY_LUNCH);
		group.add(KEY_DINNER);
		// lunch
		courseFood.add(Utils.getChildItem(lunch_course));
		// dinner
		courseFood.add(Utils.getChildItem(dinner_course));

		// selectedMaps.put(menu.getMenudate(), selectedCourseFood);
		allMap.put(menu.getMenudate(), courseFood);
		adapter = new ExpandableListAdapter(menu.getMenudate());
		expandableListView.setAdapter(adapter);

//		int count = adapter.getGroupCount();
//		for (int j = 0; j < count; j++)
//			expandableListView.expandGroup(j, true);
		/*
		service.getCoursesByIds(new TaskCallBack<List<Course>, MessageModel<List<Course>>>() {
			@Override
			public void postExecute(MessageModel<List<Course>> result) {
				if (result.isFlag()) {
					List<Course> new_courses = result.getData();

					List<Course> newLunchList = new ArrayList<Course>();
					List<Course> newDinnerList = new ArrayList<Course>();

					int i = 0;
					for (Course lCourse : new_courses) {
						if (i < lunch_course.size()) {
							newLunchList.add(lCourse);

						} else {
							newDinnerList.add(lCourse);
						}
						i++;
					}

					group.clear();
					List<List<CourseFood>> courseFood = new ArrayList<List<CourseFood>>();
					group.add(KEY_LUNCH);
					group.add(KEY_DINNER);
					// lunch
					courseFood.add(Utils.getChildItem(newLunchList));
					// dinner
					courseFood.add(Utils.getChildItem(newDinnerList));

					// selectedMaps.put(menu.getMenudate(), selectedCourseFood);
					allMap.put(menu.getMenudate(), courseFood);
					adapter = new ExpandableListAdapter(menu.getMenudate());
					expandableListView.setAdapter(adapter);

					int count = adapter.getGroupCount();
					for (int j = 0; j < count; j++)
						expandableListView.expandGroup(i, true);
				}
				pd.dismiss();
			}

			@Override
			public List<Course> getParameters() {
				List<Course> allList = new ArrayList<Course>();
				allList.addAll(lunch_course);
				allList.addAll(dinner_course);
				return allList;
			}
		});*/

	}

	class MyPagerAdapter extends PagerAdapter {
		private List<View> mViews;

		public MyPagerAdapter(List<View> views) {
			this.mViews = views;
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
		}

		@Override
		public Object instantiateItem(View container, int position) {
			try {
				((ViewPager) container).addView(
						mViews.get(position % mViews.size()), 0);
			} catch (Exception e) {
			}
			return mViews.get(position % mViews.size());
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}
	}

	//
	/**
	 * 绑定fragment
	 * 
	 * @return
	 */
	public static BuyEditorFragment newInstance() {
		BuyEditorFragment fragment = new BuyEditorFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}
}
