package com.syt.health.kitchen.fragment;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.BuyingActivity;
import com.syt.health.kitchen.FoodInfoActivity;
import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.customview.StickyScrollView;
import com.syt.health.kitchen.db.common.NutrientModel;
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

public class FoodInfoFragment extends Fragment {
	public final static String TAG = "FOODINFOFRAGMENT";
	private static final String FOODS_KEY = "FOODLIST";
	private static final String FOOD_KEY = "FOOD";
	private TextView foodName_tv;
	private ImageView foodImg;
	private TextView page_tv;
	// private Meal meal;
	private LinearLayout info_layout;
	private LinearLayout yiji_layout;
	private LinearLayout nutrient_layout;
	private LinearLayout buy_layout;
	private int currentPosition;
	private Food food;
	private ServiceImpl service;
	private List<Food> foods = new ArrayList<Food>();
	private ImageFetcher imageFetcher;
	private TextView buy_text;

	private boolean isHideCourseBtn;

	private boolean isHideNavBar;

	private LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		foods = (List<Food>) ((getArguments() != null) ? getArguments().get(
				FOODS_KEY) : null);

		if (foods != null) {
			currentPosition = (Integer) ((getArguments() != null) ? getArguments()
					.get("index") : 0);
			food = foods.get(currentPosition);
			// params.setMargins(20, 0, 0, 20);
			isHideNavBar = false;
		} else {
			food = (Food) ((getArguments() != null) ? getArguments().get(
					FOOD_KEY) : null);
			// params.setMargins(10, 30, 5, 22);
			isHideNavBar = true;
		}
		service = ((BaseActivity) getActivity()).getService();
		getNewFood(food.getId());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (getActivity() instanceof NoteActivity) {
			((NoteActivity) getActivity()).addStep();

			if (((NoteActivity) getActivity()).isSearchBadCondition()) {
				isHideCourseBtn = true;
			}
			params.setMargins(35, 0, 10, 25);

		} else if (getActivity() instanceof BuyingActivity) {
			((BuyingActivity) getActivity()).setExit(false);
			isHideCourseBtn = true;
			params.setMargins(15, 0, 15, 25);
		} else if (getActivity() instanceof FoodInfoActivity) {
			isHideCourseBtn = true;
			params.setMargins(15, 0, 15, 25);
		}

		View view = inflater.inflate(R.layout.fragment_food_info, container,
				false);
		init(view);
		return view;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void init(View view) {
		final StickyScrollView scroll = (StickyScrollView) view
				.findViewById(R.id.fragment_food_info_scroll);
		
		Button courseBtn = (Button) view
				.findViewById(R.id.fragment_food_info_course_button);
		courseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FoodCourseFragment fragment = FoodCourseFragment
						.newInstance(food.getName());
				((BaseActivity) getActivity()).addFragment(fragment,
						FoodCourseFragment.TAG, R.id.activity_main_left_linear);
			}
		});

		LinearLayout navbar = (LinearLayout) view
				.findViewById(R.id.fragment_food_info_navbar);
		if (isHideNavBar) {
			navbar.setVisibility(View.GONE);

		} else {

			food = foods.get(currentPosition);

			page_tv = (TextView) view
					.findViewById(R.id.fragment_food_info_page_tv);
			page_tv.setText(currentPosition + 1 + "/" + foods.size());

			Button preBtn = (Button) view
					.findViewById(R.id.fragment_food_info_up_button);
			preBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (currentPosition > 0) {
						currentPosition--;
						
						food = foods.get(currentPosition);
						getNewFood(food.getId());
						scroll.scrollTo(0, 0);
					}

				}
			});
			Button nextBtn = (Button) view
					.findViewById(R.id.fragment_food_info_next_button);
			nextBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (currentPosition < foods.size() - 1) {
						currentPosition++;
						food = foods.get(currentPosition);
						getNewFood(food.getId());
						scroll.scrollTo(0, 0);
					}

				}
			});
		}

		foodName_tv = (TextView) view
				.findViewById(R.id.fragment_food_info_foodname_tv);
		foodName_tv.setText(food.getName());

		foodImg = (ImageView) view
				.findViewById(R.id.fragment_food_info_food_image);

		scroll.setLayoutParams(params);

		if (isHideCourseBtn) {
			courseBtn.setVisibility(View.GONE);
		} else {
			courseBtn.setVisibility(View.VISIBLE);
		}

		info_layout = (LinearLayout) view
				.findViewById(R.id.fragment_food_info_layout);
		yiji_layout = (LinearLayout) view
				.findViewById(R.id.fragment_food_info_jieshao_layout);
		nutrient_layout = (LinearLayout) view
				.findViewById(R.id.fragment_food_info_shicai_layout);
		buy_layout = (LinearLayout) view
				.findViewById(R.id.fragment_food_info_step_layout);
		buy_text = (TextView) view
				.findViewById(R.id.fragment_food_info_step_txt);

		Button infoIntroBtn = (Button) view
				.findViewById(R.id.fragment_food_info_intro_btn);
		infoIntroBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scroll.scrollTo(0, ((View) yiji_layout.getParent()).getTop()
						- info_layout.getHeight());

			}
		});
		Button infoFoodBtn = (Button) view
				.findViewById(R.id.fragment_food_info_food_btn);
		infoFoodBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scroll.scrollTo(0,
						((View) nutrient_layout.getParent()).getTop()
								- info_layout.getHeight());

			}
		});
		Button infoStepBtn = (Button) view
				.findViewById(R.id.fragment_food_info_step_btn);
		infoStepBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scroll.scrollTo(0, ((View) buy_layout.getParent()).getTop()
						- info_layout.getHeight());

			}
		});
		Button infoTopBtn = (Button) view
				.findViewById(R.id.fragment_food_info_top_btn);
		infoTopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				scroll.scrollTo(0, 0);

			}
		});
	}

	/**
	 * 绑定fragment
	 * 
	 * @param meal
	 * @param index
	 * @return
	 */
	public static FoodInfoFragment newInstance(ArrayList<Food> fList, int index) {
		FoodInfoFragment fragment = new FoodInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(FOODS_KEY, fList);
		bundle.putInt("index", index);
		fragment.setArguments(bundle);
		return fragment;
	}

	public static FoodInfoFragment newInstance(Food food) {
		FoodInfoFragment fragment = new FoodInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(FOOD_KEY, food);
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
	 * 
	 * @param food
	 */
	private void initializeData(Food food) {
		if(getActivity() == null) return;
		
		foodName_tv.setText(food.getName());
		imageFetcher.loadImage(food.getPicurl(), foodImg);
		yiji_layout.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		TextView course_effectivity = new TextView(getActivity());

		List<String> hList;
		if (getActivity() instanceof BuyingActivity) {
			hList = ((BuyingActivity) getActivity()).getHealthList();
		} else if (service.getCurrentMenu() == null
				|| (getActivity() instanceof NoteActivity && ((NoteActivity) getActivity())
						.isHealthBible())) {
			hList = service.getCurrentUser().getObjSmartParams()
					.getHealthcondition();
		} else {
			hList = service.getCurrentMenu().getSmartParams()
					.getHealthcondition();
		}
		String shiyi = Utils.arrayIntoString(food.getEffectivity());

		String tmpHealth = (getActivity() instanceof NoteActivity) ? ((NoteActivity) getActivity())
				.getCurrentHealthCondition() : null;
		List<String> strList = food.getGoodDesc(hList);
		if (tmpHealth != null) {
			strList.add(tmpHealth);
		}
		for (String spShiyi : strList) {
			if (shiyi.contains(spShiyi)) {
				shiyi = shiyi.replace(spShiyi,
						"<span style=\"background-color:#f88855\"><font color=\"#f88855\">"
								+ spShiyi + "</font></span>");
			}
		}
		course_effectivity.setText(Html.fromHtml("适宜人群：<BR>" + shiyi));
		course_effectivity.setLayoutParams(params);
		course_effectivity.setTextSize(getResources().getDimension(
				R.dimen.food_info_text_size));
		course_effectivity.setTextColor(getResources().getColor(
				android.R.color.black));
		yiji_layout.addView(course_effectivity);
		TextView course_incompatible = new TextView(getActivity());

		String buyi = Utils.arrayIntoString(food.getIncompatible());
		strList = food.getBadDesc(hList);
		if (tmpHealth != null) {
			strList.clear();
			strList.add(tmpHealth);
		}
		for (String spBuyi : strList) {
			if (buyi.contains(spBuyi)) {
				buyi = buyi.replace(spBuyi,
						"<span style=\"background-color:#f88855\"><font color=\"#f88855\">"
								+ spBuyi + "</font></span>");
			}
		}
		course_incompatible.setText(Html.fromHtml("不宜人群：<BR>" + buyi));
		course_incompatible.setLayoutParams(params);
		course_incompatible.setTextSize(getResources().getDimension(
				R.dimen.food_info_text_size));
		course_incompatible.setTextColor(getResources().getColor(
				android.R.color.black));
		yiji_layout.addView(course_incompatible);

		List<NutrientModel> nList = food.getNutrients();
		String nutrient = "";
		for (NutrientModel nut : nList) {
			nutrient += nut.getName() + ":"
					+ String.format("%.2f", nut.getContent() * 100)
					+ nut.getUnit() + "\n";
		}
		nutrient_layout.removeAllViews();
		TextView shicai_tv = new TextView(getActivity());
		shicai_tv.setText(nutrient);
		shicai_tv.setLayoutParams(params);
		shicai_tv.setTextSize(getResources().getDimension(
				R.dimen.food_info_text_size));
		shicai_tv.setTextColor(getResources().getColor(android.R.color.black));
		nutrient_layout.addView(shicai_tv);

		buy_layout.removeAllViews();
		String bible = food.getBibletext();
		if (bible != null && bible.length() > 0) {
			buy_text.setText(R.string.buy);
			TextView bibleTxt = new TextView(getActivity());
			bibleTxt.setText(bible);
			bibleTxt.setLayoutParams(params);
			bibleTxt.setTextSize(getResources().getDimension(
					R.dimen.food_info_text_size));
			bibleTxt.setTextColor(getResources()
					.getColor(android.R.color.black));
			buy_layout.addView(bibleTxt);
		} else {
			buy_text.setText(R.string.buy_no); 
		}
	}

	/**
	 * 根据Food的id查找对应的Food
	 * 
	 * @param id
	 */
	private void getNewFood(final String id) {
		service.getFoodById(new TaskCallBack<String, MessageModel<Food>>() {
			@Override
			public void postExecute(MessageModel<Food> result) {
				if (result.isFlag()) {
					food = result.getData();
					initializeData(food);
					if(page_tv!=null){
						page_tv.setText(currentPosition + 1 + "/"
								+ foods.size());
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
