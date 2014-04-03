package com.syt.health.kitchen.fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.BuyingActivity;
import com.syt.health.kitchen.FoodInfoActivity;
import com.syt.health.kitchen.MoreActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SetConditionActivity;
import com.syt.health.kitchen.SetFruitActivity;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.json.Course;
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
import com.syt.health.kitchen.widget.CoverFlowView;
import com.syt.health.kitchen.widget.MyHorizontalScrollView;
import com.syt.health.kitchen.widget.QuickAction;
import com.syt.health.kitchen.widget.MyHorizontalScrollView.SizeCallback;
import com.syt.health.kitchen.widget.PredicateLayout;

public class NoteFragment extends Fragment implements RefreshableFragment {
	public static final String TAG = "NOTEFRAGMENT";
	
	private TextView condition_tv;
	private TextView numPeople_tv;
	private TextView date_tv;
	private String currentDate;
	private List<String> availableDateList;
	private ServiceImpl service;
	private Menu menu;
	private List<String> selConditionListStr;

	private GenerateCondition conditionParam;

	private MyHorizontalScrollView final_scrollView;
	private LinearLayout fruit_layout;
	private Button recommend_btn;
	private TextView recFruitCalorie_tv;// 推荐水果热量
	private TextView totalFruitCalorie_tv;// 选择水果的总热量
	private TextView comments_tv;
	private ListView chartLL;
	private Button health_btn;
	private Button setCondition_btn;
	private boolean footerFlag = true;
	private boolean readerFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		service = ((BaseActivity) getActivity()).getService();
		if (((NoteActivity) getActivity()).getDate() != null) {
			currentDate = ((NoteActivity) getActivity()).getDate();
		} else {
			currentDate = DateUtils.defaultFormat(new Date());
			((NoteActivity) getActivity()).setDate(currentDate);
		}
		
		readerFlag = ((NoteActivity)getActivity()).isReadOnly();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_note_left_info,
				container, false);
		init(view);	
		if(getActivity() != null){
		((NoteActivity) getActivity()).setStep(0);
		((NoteActivity)getActivity()).setCurrentHealthCondition(null);
		((NoteActivity)getActivity()).setSearchBadCondition(false);
		}
		return view;
	}

	/**
	 * 初始化控件并给控件设置对应的事件
	 * 
	 * @param view
	 */
	private void init(View view) {
		
		Button buylist_btn = (Button) view.findViewById(R.id.activity_main_buying_btn);
		buylist_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), BuyingActivity.class);
				boolean flag = true;
				intent.putExtra(BuyingListFragment.TAG, flag);
				startActivity(intent);
			}
		});
		Button more_btn = (Button) view.findViewById(R.id.activity_main_more_btn);
		more_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), MoreActivity.class);
				startActivity(intent);	
			}
		});
		final_scrollView = (MyHorizontalScrollView)view
				.findViewById(R.id.activity_main_myscrollview);
		View health_value = LayoutInflater.from(getActivity()).inflate(
				R.layout.notefragment_health_value, null);
		View recommend_fruit = LayoutInflater.from(getActivity()).inflate(
				R.layout.notefragment_recommend_fruit, null);
		health_btn = (Button)health_value.findViewById(R.id.health_comments_btn);
		recommend_btn = (Button) recommend_fruit
				.findViewById(R.id.notefragment_recommend_fruit_btn);
		recommend_btn.setBackgroundResource(R.drawable.fruit);
		health_btn.setOnClickListener(new ClickListenerForScrolling(
				final_scrollView, health_value, health_btn));
		comments_tv = (TextView) health_value
				.findViewById(R.id.health_comments);   

		final View[] children = { recommend_fruit, health_value };
		int scrollToViewIdx = 1;
		final_scrollView.initViews(children, scrollToViewIdx,
				new SizeCallbackForMenu(recommend_btn));
		condition_tv = (TextView) view
				.findViewById(R.id.activity_main_condition_tv);

		recFruitCalorie_tv = (TextView) recommend_fruit
				.findViewById(R.id.notefragment_recommend_fruit_calorie_tv);
		totalFruitCalorie_tv = (TextView) recommend_fruit
				.findViewById(R.id.notefragment_total_fruit_calorie_tv);
		fruit_layout = (LinearLayout) recommend_fruit
				.findViewById(R.id.notefragment_recommend_fruit_layout);
		final Button preDay_btn = (Button) view
				.findViewById(R.id.activity_main_date_up_button);

		final Button nextDay_btn = (Button) view
				.findViewById(R.id.activity_main_date_next_button);
		setCondition_btn = (Button)view.findViewById(R.id.activity_main_set_condition_btn);
		setCondition_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), SetConditionActivity.class);
				intent.putExtra(Utils.DATE_KEY,currentDate);
				startActivity(intent);
			}
		});
		preDay_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentDate.equals(availableDateList.get(0))) {
					Toast.makeText(getActivity(), R.string.no_menu_data,
							Toast.LENGTH_LONG).show();
					v.setEnabled(false);
					preDay_btn.setBackgroundResource(R.drawable.date_up_button_bg_disable);
				} else {
					currentDate = availableDateList.get(availableDateList
							.indexOf(currentDate) - 1);
					gotoDate(currentDate);
					nextDay_btn.setEnabled(true);
					preDay_btn.setBackgroundResource(R.drawable.date_up_button);
					nextDay_btn.setBackgroundResource(R.drawable.date_next_button);
				}
			}
		});

		nextDay_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentDate.equals(availableDateList.get(availableDateList
						.size() - 1))) {
					Toast.makeText(getActivity(), R.string.no_menu_data,
							Toast.LENGTH_LONG).show();
					v.setEnabled(false);
					nextDay_btn.setBackgroundResource(R.drawable.date_next_button_bg_disable);
				} else {
					currentDate = availableDateList.get(availableDateList
							.indexOf(currentDate) + 1);
					gotoDate(currentDate);
					preDay_btn.setEnabled(true);
					nextDay_btn.setBackgroundResource(R.drawable.date_next_button);
					preDay_btn.setBackgroundResource(R.drawable.date_up_button);
				}
			}
		});

		chartLL = (ListView) view.findViewById(R.id.activity_main_chart_list);
		chartLL.setDivider(null);

		date_tv = (TextView) view.findViewById(R.id.activity_main_date_tv);
		date_tv.setText(currentDate);
		availableDateList = new ArrayList<String>();
		List<String> list = service.getMenuIn30Days();
		if (list != null) {
			availableDateList.addAll(list);
		}

		Date nowDate = new Date();
		for (int i = 0; i < 7; i++) {
			availableDateList.add(DateUtils.defaultFormat(DateUtils
					.addDateDays(nowDate, i)));
		}


		numPeople_tv = (TextView) view
				.findViewById(R.id.activity_main_numpeople_btn);
		
		
		
		
		final LinearLayout below_layout = (LinearLayout)view.findViewById(R.id.activity_main_lv_below_layout);
		final LinearLayout footer_layout = (LinearLayout)view.findViewById(R.id.activity_main_lv_footer_layout);
		footer_layout.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(footerFlag){
					footerFlag = false;
					footer_layout.setBackgroundResource(R.drawable.note_list_up_state);
					below_layout.setVisibility(View.GONE);
				}else{
					footerFlag = true;
					footer_layout.setBackgroundResource(R.drawable.note_list_down_state);
					below_layout.setVisibility(View.VISIBLE);
				}
			}
		});

	} 

	private void createNutrientCharts() {
		final List<NutrientModel> nList = menu.getNutrients();
		NutrientListAdapter adapter = new NutrientListAdapter(getActivity(), nList);
		chartLL.setAdapter(adapter);

		chartLL.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				QuickAction mAction = new QuickAction(getActivity());
				ActionItem item = new ActionItem(0, nList.get(position)
						.getRangeDesc(conditionParam.getPeople()));
				mAction.addActionItem(item);
				mAction.show(v);

			}
		});
	}

	class NutrientListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<NutrientModel> mList;

		public NutrientListAdapter(Context context, List<NutrientModel> list) {
			mInflater = LayoutInflater.from(context);
			mList = list;
		}

		@Override
		public int getCount() {
			return mList.size() > 0 ? mList.size() : 1;
		}

		@Override
		public NutrientModel getItem(int position) {
			return mList.size() > 0 ? mList.get(position) : null;
		}

		@Override
		public long getItemId(int position) {
			return mList.size() > 0 ? mList.get(position).getId() : 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mList == null || position < 0 || position >= mList.size()) {
				TextView title = new TextView(parent.getContext());
				title.setText(R.string.no_nutrients_data);
				title.setTextSize(18);
				return title;
			}
			// if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_chart_list_item,
					null);
			// }
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if (holder == null || holder.name == null) {
				holder = new ViewHolder(convertView);
				if (holder.name == null) {
					convertView = mInflater.inflate(
							R.layout.fragment_chart_list_item, null);
					holder = new ViewHolder(convertView);
				}
				convertView.setTag(holder);
			}

			// fill in data
			if(position==0){
				holder.layout.setVisibility(View.VISIBLE);
			}else{
				holder.layout.setVisibility(View.GONE);
			}
			final NutrientModel nm = mList.get(position);
			holder.name.setText(nm.getName() + ":");
			String desc = nm.getCurrentPositionDesc(conditionParam.getPeople());
			if (NutrientModel.FIELD_START.equals(desc)) {
				holder.badge.setImageResource(R.drawable.badge_bad);
				holder.progress.setProgressDrawable(getResources().getDrawable(
						R.drawable.progress_bar_states_red));

			} else if (NutrientModel.FIELD_GOOD.equals(desc)) {
				holder.progress.setProgressDrawable(getResources().getDrawable(
						R.drawable.progress_bar_states_yellow));
				holder.badge.setImageResource(R.drawable.badge_good);
			} else if (NutrientModel.FIELD_EXCESS.equals(desc)) {
				holder.badge.setImageResource(R.drawable.badge_more);
				holder.progress.setProgressDrawable(getResources().getDrawable(
						R.drawable.progress_bar_states_red));

			} else {
				holder.progress.setProgressDrawable(getResources().getDrawable(
						R.drawable.progress_bar_states_green));
				holder.badge.setImageResource(R.drawable.badge_perfect);
			}
			holder.value.setText(String.format("%.2f", nm.getContent())
					+ nm.getUnit());
			if (nm.getGood() < 0 || nm.getExcess() < 0) {
				// other nutrients
				holder.grid.setImageResource(R.drawable.pb_three);
			} else {
				// cals
				holder.grid.setImageResource(R.drawable.pb_five);
			}

			holder.progress.setProgress(nm.getCurrentValue(conditionParam.getPeople()));

			return convertView;
		}
	}

	class ViewHolder {
		TextView name = null;
		ProgressBar progress = null;
		TextView value = null;
		ImageView badge = null;
		ImageView grid = null;
		LinearLayout layout;

		ViewHolder(View base) {
			this.name = (TextView) base
					.findViewById(R.id.fragment_chart_list_item_title_txt);
			this.progress = (ProgressBar) base
					.findViewById(R.id.fragment_chart_list_item_pb);
			this.value = (TextView) base
					.findViewById(R.id.fragment_chart_list_item_value_txt);
			this.badge = (ImageView) base
					.findViewById(R.id.fragment_chart_list_item_badge_img);
			this.grid = (ImageView) base
					.findViewById(R.id.fragment_chart_list_item_grid_img);
			this.layout = (LinearLayout)base.findViewById(R.id.fragment_chart_list_item_header_layout);
		}

	}

	private void gotoDate(String date) {
		
		fetchMenu(date);
	}

	/***
	 * 根据日期和养生条件从服务器获取Menu
	 * 
	 * @param date
	 */
	private void fetchMenu(final String date) {
		final ProgressDialog pd = ProgressDialog.show(getActivity(),null,
				null);
		
		service.fetchMealsByDate(new TaskCallBack<String, MessageModel<Menu>>() {
			@Override
			public void postExecute(MessageModel<Menu> result) {
				if (result.isFlag()) {
					if( getActivity() == null) return;
					
					((NoteActivity) getActivity()).setDate(date);
					date_tv.setText(date);
					TextView today_tv = (TextView) getActivity().findViewById(
							R.id.activity_main_today_date_btn);
					today_tv.setText(DateUtils.getMonthDay(date, true));
					
					Menu menu = result.getData();
					refreshData(menu);
				} else {
					Toast.makeText(getActivity(),
							result.getMessage(),
							Toast.LENGTH_LONG).show();  
				}
				pd.dismiss();
			}

			@Override
			public String getParameters() {
				return date;
			}
		});
	}

	public void refreshData(Menu newMenu) {
		if(getActivity() == null) return;
		
		menu = newMenu;
		((NoteActivity) getActivity()).setMenu(menu);
		readerFlag = ((NoteActivity)getActivity()).isReadOnly();
		//UserModel userModel = service.getCurrentUser();
		conditionParam = menu.getSmartParams();
		if(conditionParam!=null){
			selConditionListStr = conditionParam.getHealthcondition();
			numPeople_tv.setText(String.valueOf(conditionParam.getPeople()));
		}else{
			conditionParam = new GenerateCondition();
			conditionParam.setPeople(Integer.parseInt(numPeople_tv.getText().toString()));
			selConditionListStr = new ArrayList<String>();
		}
		condition_tv.setText(getResources().getString(R.string.health_requirement)+Utils.arrayIntoString(selConditionListStr));
		recFruitCalorie_tv.setText("推荐热量:"+new Double(menu.getFruitAdviceCal(conditionParam.getPeople())).intValue()+"千卡");
		totalFruitCalorie_tv.setText("已选热量:"+new Double(menu.getTotalFruitCal()).intValue()+"千卡");
		fruit_layout.removeAllViews();
		Meal fruitMeal = menu.getFruit();
		final List<MealCourse> fruit_meaCourses = fruitMeal.getItems();
		for (int i = 0; i < fruit_meaCourses.size(); i++) {	
			
			LinearLayout layout = new LinearLayout(getActivity());
			layout.setLayoutParams(new LayoutParams((int)getResources().getDimension(R.dimen.fruit_bg_width),
					(int)getResources().getDimension(R.dimen.fruit_bg_height)));
			layout.setOrientation(LinearLayout.VERTICAL);
			final TextView name = new TextView(getActivity());
			name.setGravity(Gravity.CENTER);
			TextView weight = new TextView(getActivity());
			weight.setGravity(Gravity.CENTER_HORIZONTAL);
			String str = fruit_meaCourses.get(i).getCourse().getName();
			name.setText(str);
			name.setTextSize(getResources().getDimension(R.dimen.fruit_name_text_size));
			name.setTextColor(Color.BLACK);
			weight.setText(new Double(
					fruit_meaCourses.get(i).getQuantity() * 100).intValue()
					+ "g");
			weight.setTextColor(Color.BLACK);
			if(fruit_meaCourses.get(i).getCourse().getBadDesc(conditionParam.getHealthcondition()).size()>0){
				layout.setBackgroundResource(R.drawable.bad_fruitbtnbg);
			}else{
				layout.setBackgroundResource(R.drawable.good_fruitbtnbg);
			}
			layout.addView(name);
			layout.addView(weight);
			layout.setTag(i);
			if (!((NoteActivity) getActivity()).isReadOnly()) {
				layout.setEnabled(true);
			} else {
				layout.setEnabled(false);
			}
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					int position = (Integer) v.getTag();
					List<Food> fList = Utils.convertFruitCourse(fruit_meaCourses);
				    Intent intent = new Intent();
					intent.setClass(getActivity(), FoodInfoActivity.class);
					intent.putExtra("index", position);
					intent.putExtra(FoodInfoActivity.FOODS_KEY, (ArrayList<Food>)fList);
					startActivity(intent);

				}
			});
			fruit_layout.addView(layout);
		}
		comments_tv.setText(Html.fromHtml(menu.getComments()));
		Button addFruit_btn = new Button(getActivity());
		addFruit_btn.setLayoutParams(new LayoutParams((int)getResources().getDimension(R.dimen.fruit_bg_width),
				(int)getResources().getDimension(R.dimen.fruit_bg_height)));
		addFruit_btn.setBackgroundResource(R.drawable.fruit_add_bg);
		if (!((NoteActivity) getActivity()).isReadOnly()) {
			addFruit_btn.setEnabled(true);
		} else {
			addFruit_btn.setEnabled(false);
		}
		addFruit_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), SetFruitActivity.class);
					intent.putExtra(Utils.DATE_KEY, menu.getMenudate());
					startActivity(intent);
//				((BaseActivity) getActivity()).addFragment(FruitAddFragment
//						.newInstance(),
//						FruitAddFragment.TAG, R.id.activity_main_left_linear);
			}
		});
		fruit_layout.addView(addFruit_btn);
		
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				health_btn.performClick();
//			}
//		}, 100);    
		if(readerFlag){
			setCondition_btn.setEnabled(false);			
		}else{
			setCondition_btn.setEnabled(true);
		}
		createNutrientCharts();
		// PieChart();
	}

	/**
	 * 给fragment绑定数据
	 * 
	 * @return
	 */
	public static NoteFragment newInstance() {
		NoteFragment fragment = new NoteFragment();
		Bundle bundle = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
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
				btn.setBackgroundResource(R.drawable.today_reviewbtn_inactive);

			} else {
				// Scroll to menuWidth so menu isn't on screen.
				int left = menuWidth;
				scrollView.smoothScrollTo(left,0);
				btn.setBackgroundResource(R.drawable.today_reviewbtn_active);
			}
			menuOut = !menuOut;
		}
	}

	/**
	 * Helper that remembers the width of the 'slide' button, so that the
	 * 'slide' button remains in view, even when the menu is showing.
	 */
	static class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnSlide;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnSlide = btnSlide;
		}   

		@Override
		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth();
			System.out.println("btnWidth=" + btnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			final int menuIdx = 0;
			if (idx == menuIdx) {
				dims[0] = w - btnWidth;  
			}
		}
	}
	@Override
	public void onResume() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				health_btn.performClick();
			}
		}, 100);
		SharedPreferences sp = getActivity().getSharedPreferences("sp_first",0);
		LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.activity_main_help_layout);
		if(sp.getBoolean(Utils.NOTE_FLAG_02, false)){
			Utils.addImageView(getActivity(), layout, R.drawable.help_note_02, 0, Utils.NOTE_FLAG_02, sp);
		}
		Menu menu2 = service.getCurrentMenu();
		if(menu2 != null){
			menu = menu2;
			refreshData(menu);
		}else{
			fetchMenu(currentDate);
		}
		super.onResume();
	}
}
