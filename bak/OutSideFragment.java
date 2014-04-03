package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.HistoryActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.db.RestaurantModel;
import com.syt.health.kitchen.db.common.ComnCourseModel;
import com.syt.health.kitchen.db.common.FoodModel;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.GoodBadConflictComparable;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.service.param.SmartMealConditionParam;
import com.syt.health.kitchen.utils.DateUtils;
import com.syt.health.kitchen.utils.Utils;

public class OutSideFragment extends Fragment {
	private static final String TAG = "OutSideFragment";
	private static String PARAMS_DATA_EXTRA = "params";

	private SmartMealConditionParam params;
	private AutoCompleteTextView inputTxt;
	private ExpandableListView addList;
	private List<Course> courseList;
	private List<Food> foodList;
	private TextView descTxt;

	private boolean isSearchCourse = true;

	private ServiceImpl service;

	public static OutSideFragment newInstance(SmartMealConditionParam params) {
		final OutSideFragment f = new OutSideFragment();

		final Bundle args = new Bundle();
		args.putSerializable(PARAMS_DATA_EXTRA, params);
		f.setArguments(args);

		return f;
	}

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public OutSideFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		params = (getArguments() != null ? (SmartMealConditionParam) getArguments()
				.getSerializable(PARAMS_DATA_EXTRA) : null);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		service = ((BaseActivity) activity).getService();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.fragment_outside, container,
				false);

		TextView titleTxt = (TextView) v.findViewById(R.id.outsideTitleTxt);
		titleTxt.setText(DateUtils.defaultFormat(new Date()));
		descTxt = (TextView) v.findViewById(R.id.outsideDescTxt);

		inputTxt = (AutoCompleteTextView) v.findViewById(R.id.outsideInputTxt);
		final AutoCompleteAdapter adapter = new AutoCompleteAdapter(
				getActivity(), R.layout.autotext_item);
		adapter.setService(service);
		inputTxt.setAdapter(adapter);
		inputTxt.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View textView,
					int position, long arg3) {
				inputTxt.setTag(adapter.getValues().get(position));

			}
		});

		Button addBtn = (Button) v.findViewById(R.id.outsideAddBtn);
		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Object item = inputTxt.getTag();
				if (item != null) {
					if (item instanceof ComnCourseModel) {
						service.getCourseById(new TaskCallBack<String, MessageModel<Course>>() {

							@Override
							public String getParameters() {
								return ((ComnCourseModel) item).getId();
							}

							@Override
							public void postExecute(MessageModel<Course> result) {
								if (result.isFlag()) {
									Utils.listAdd(result.getData(), courseList);

									refreshList();
								} else {
									// TODO: prompt
								}

							}
						});
					} else if (item instanceof FoodModel) {
						service.getFoodById(new TaskCallBack<String, MessageModel<Food>>() {

							@Override
							public String getParameters() {
								return ((FoodModel) item).getId();
							}

							@Override
							public void postExecute(MessageModel<Food> result) {
								if (result.isFlag()) {
									Utils.listAdd(result.getData(), foodList);

									refreshList();
								} else {
									// TODO: prompt
								}

							}
						});
					}

				}

			}
		});

		RadioButton courseRadio = (RadioButton) v
				.findViewById(R.id.outsideAddCourseRadio);
		courseRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isSearchCourse = isChecked;

			}
		});

		addList = (ExpandableListView) v
				.findViewById(R.id.outsideCourseFoodList);

		initList();

		Button resetBtn = (Button) v.findViewById(R.id.outsideResetBtn);
		resetBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				courseList = new ArrayList<Course>();
				foodList = new ArrayList<Food>();

				refreshList();
			}
		});

		Button saveBtn = (Button) v.findViewById(R.id.outsideSaveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final EditText input = new EditText(getActivity());

				new AlertDialog.Builder(getActivity())
						// .setTitle("Update Status")
						.setMessage(R.string.input_restaurant)
						//
						.setView(input)
						//
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {//
									public void onClick(DialogInterface dialog,
											int whichButton) {//
										Editable value = input.getText(); //

										if (value == null
												|| value.length() == 0) {//
											toastMsg(R.string.restaurant_name_null);//
										} else {//
											// save restaurant
											RestaurantModel resturant = new RestaurantModel();//
											resturant.setName(value.toString());//
											resturant.setCourseList(courseList);//
											resturant.setFoodList(foodList);//
											service.saveOutsideHistoryList(resturant);//
											startActivity(new Intent(getActivity(), HistoryActivity.class));
										}
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Do nothing.
									}
								}).show();

			}
		});

		return v;
	}

	private void initList() {
		courseList = new ArrayList<Course>();
		foodList = new ArrayList<Food>();

		// load data from db
		refreshList();
	}

	private void refreshList() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				MyExpandableListAdapter adapter = new MyExpandableListAdapter();
				addList.setAdapter(adapter);
				addList.expandGroup(0);
				addList.expandGroup(1);
				// ((BaseAdapter) addList.getAdapter()).notifyDataSetChanged();

			}
		});

	}
	
	private List<GoodBadConflictComparable> convertSelList(){
		List<GoodBadConflictComparable> list = new ArrayList<GoodBadConflictComparable>();
		for(Food food : foodList){
			list.add(food);
		}
		for(Course course : courseList){
			list.add(course);
		}
		return list;
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		public MyExpandableListAdapter() {
		}

		public Object getChild(int groupPosition, int childPosition) {
			switch (groupPosition) {
			case 0: {
				return courseList.get(childPosition);
			}
			case 1: {
				return foodList.get(childPosition);
			}
			default: {
				return null;
			}
			}
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			switch (groupPosition) {
			case 0: {
				return courseList.size();
			}
			case 1: {
				return foodList.size();
			}
			default: {
				return 0;
			}
			}
		}

		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, 64);

			TextView textView = new TextView(getActivity());
			textView.setLayoutParams(lp);

			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(72, 0, 0, 0);
			textView.setTextSize(20);
			return textView;
		}

		public View getChildView(int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			LinearLayout layout = new LinearLayout(getActivity());

			TextView nameTxt = new TextView(getActivity());
			layout.addView(nameTxt);

			Button goodsBtn = new Button(getActivity());
			goodsBtn.setText(R.string.goods);
			layout.addView(goodsBtn);

			Button badsBtn = new Button(getActivity());
			badsBtn.setText(R.string.bads);
			layout.addView(badsBtn);

			Button conflictBtn = new Button(getActivity());
			conflictBtn.setText(R.string.conflicts);
			layout.addView(conflictBtn);

			Button deleteBtn = new Button(getActivity());
			deleteBtn.setText(R.string.delete);
			layout.addView(deleteBtn);

			if (groupPosition == 0) {
				if (childPosition >= courseList.size())
					return null;

				Course course = courseList.get(childPosition);
				nameTxt.setText(course.getName());
				deleteBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						courseList.remove(childPosition);
						refreshList();
					}
				});

				if (course.getGoodDesc(params).size() > 0) {
					goodsBtn.setVisibility(View.VISIBLE);
				} else {
					goodsBtn.setVisibility(View.GONE);
				}
				
				if (course.getBadDesc(params).size() > 0) {
					badsBtn.setVisibility(View.VISIBLE);
				} else {
					badsBtn.setVisibility(View.GONE);
				}
				
				if (course.getConflictDesc(convertSelList()).size() > 0) {
					conflictBtn.setVisibility(View.VISIBLE);
				} else {
					conflictBtn.setVisibility(View.GONE);
				}
			} else {
				if (childPosition >= foodList.size())
					return null;

				Food food = foodList.get(childPosition);
				nameTxt.setText(food.getName());
				deleteBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						foodList.remove(childPosition);
						refreshList();
					}
				});
				
				if (food.getGoodDesc(params).size() > 0) {
					goodsBtn.setVisibility(View.VISIBLE);
				} else {
					goodsBtn.setVisibility(View.GONE);
				}
				
				if (food.getBadDesc(params).size() > 0) {
					badsBtn.setVisibility(View.VISIBLE);
				} else {
					badsBtn.setVisibility(View.GONE);
				}
				if (food.getConflictDesc(convertSelList()).size() > 0) {
					conflictBtn.setVisibility(View.VISIBLE);
				} else {
					conflictBtn.setVisibility(View.GONE);
				}
			}

			return layout;
		}

		public Object getGroup(int groupPosition) {
			switch (groupPosition) {
			case 0: {
				return courseList;
			}
			case 1: {
				return foodList;
			}
			default: {
				return null;
			}
			}
		}

		public int getGroupCount() {
			return 2;
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = getGenericView();
			textView.setText((groupPosition == 0) ? R.string.course
					: R.string.food);
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

		public boolean hasStableIds() {
			return true;
		}

		public void onGroupCollapsed(int groupPosition) {
		}

		public void onGroupExpanded(int groupPosition) {
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
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	class AutoCompleteAdapter extends ArrayAdapter<String> implements
			Filterable {
		public AutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		private List values = new ArrayList();
		private ServiceImpl service;

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						if (isSearchCourse) {
							values = service.queryCourseByLikeWords(
									constraint.toString(), 0);
						} else {
							values = service.queryCourseByLikeWords(
									constraint.toString(), 0);
						}
						filterResults.values = values;
						filterResults.count = values.size();
					}
					return filterResults;
				}
			};
			return filter;
		}

		@Override
		public int getCount() {
			return values.size();
		}

		@Override
		public String getItem(int position) {
			return values.get(position).toString();
		}

		public List getValues() {
			return values;
		}

		public ServiceImpl getService() {
			return service;
		}

		public void setService(ServiceImpl service) {
			this.service = service;
		}
	}
}
