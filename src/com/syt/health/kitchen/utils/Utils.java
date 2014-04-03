/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syt.health.kitchen.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syt.health.kitchen.NoteActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Class containing some static utility methods.
 */
public class Utils {

	public static final int STANDARD_CALS = 2250;// 每人每天推荐摄入热量
	// 类型
	public static final int BREAKFAST = 1;
	public static final int LUNCH = 2;
	public static final int DINNER = 3;
	// 传参key
	public static final String MEAL_TYPE_KEY = "type";
	public static final String DATE_KEY = "date";
	public static final String COURSE_CONDITION_ZAODIAN = "早点";
	public static final String COURSE_CONDITION_ZHUSHI = "主食";
	public static final String COURSE_CONDITION_FRUIT = "水果";
	public static final String COURSE_CONDITION_ZZF = "水果主食早点";
	public static final String COURSE_TYPE_ZHENGCAN = "正餐";
	public static final String COURSE = "course";
	public static final String MEAL_LUNCH = "中餐";
	public static final String MEAL_DINNER = "晚餐";
	public static final String MEALCOURSE = "mealCourse";
	public static final String RE_LIANG = "热量";
	public static final String HEALTHADVICE = "true";

	public static final String HEALTH_YOUTH = "青少年";
	public static final String HEALTH_OPT = "术后调养";
	public static final String HEALTH_POOR_BLOOD = "贫血";
	public static final String HEALTH_SUP_BLOOD = "补血";
	public static final String HEALTH_BEAUTY = "美容";
	public static final String HEALTH_PREG = "孕妇";
	public static final String HEALTH_PRE_PREG = "备孕期";
	public static final String HEALTH_KIDEY = "肾炎";
	public static final String HEALTH_DIABETES = "糖尿病";
	public static final String HEALTH_OLD = "老年人";
	public static final String HEALTH_MENO_PAUSE = "更年期";
	public static final String NUTRIENT_PROTEIN = "蛋白质";
	public static final String NUTRIENT_IRON = "铁";
	public static final String NUTRIENT_VC = "维C";
	public static final String NUTRIENT_FOLIC = "叶酸";

	public static final String YOU_ER = "幼儿";

	public static final List<String> FRUIT_FOUR_SEASON = Arrays
			.asList(new String[] { "春", "夏", "秋", "冬" });
	public static final String GUIDE_FLAG ="guide_flag";
	public static final String NOTE_FLAG_01 ="note_flag_01";
	public static final String NOTE_FLAG_02 ="note_flag_02";
	public static final String MEAL_FLAG ="meal_flag";
	public static final String MAIN_FLAG ="main_flag";
	public static final String SETCONDITION_FLAG = "setcondition_flag";
	public static final String SETFRUIT_FALG = "setfruit_flag";
	public static final String ADD_COURSE_FALG = "add_course_flag";
	public static final String HEALTHCONDITION_FLAG ="healthcondition_flag";
	private static final String TAG = "Utils";
	private Utils() {
	};

	@TargetApi(11)
	public static void enableStrictMode() {
		if (Utils.hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog();

			if (Utils.hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
				// vmPolicyBuilder
				// .setClassInstanceLimit(DailyArtWorkActivity.class, 1)
				// .setClassInstanceLimit(ImageDetailActivity.class, 1);
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static String arrayIntoString(List<String> lists) {
		StringBuilder sb = new StringBuilder();
		if (lists != null) {
			for (int i = 0; i < lists.size(); i++) {
				sb.append(lists.get(i)).append(",");
			}
			if (sb.toString().endsWith(",")) {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		}

		return "";
	}

	public static String arrayIntoString(String[] strs) {
		StringBuilder sb = new StringBuilder();
		if (strs != null) {
			for (int i = 0; i < strs.length; i++) {
				sb.append(strs[i]).append(",");
			}
			if (sb.toString().endsWith(",")) {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		}
		return null;
	}

	public static List<Course> convertMealCourse(List<MealCourse> list) {
		ArrayList<Course> res = new ArrayList<Course>();
		if (list == null)
			return res;
		for (MealCourse mc : list) {
			res.add(mc.getCourse());
		}
		return res;
	}

	public static int calculatetMealCals(List<MealCourse> list) {
		if (list == null)
			return 0;
		double cals = 0;
		for (MealCourse mc : list) {
			if (YOU_ER.equals(mc.getType()))
				continue;
			int cal = mc.getCourse().getCalories();
			cals += mc.getQuantity() * cal;
		}
		return (int) cals;
	}

	public static int calculatetMealCals(List<MealCourse> list, int mainCals) {
		if (list == null)
			return 0;
		double cals = 0;
		boolean isAddMainCals = true;
		for (MealCourse mc : list) {
			if (YOU_ER.equals(mc.getType()))
				continue;
			if (mc.getCourse().getCoursecond().equals(COURSE_CONDITION_ZHUSHI)
					|| mc.getCourse().getCoursecond()
							.equals(COURSE_CONDITION_ZAODIAN)) {
				isAddMainCals = false;
			}
			int cal = mc.getCourse().getCalories();
			cals += mc.getQuantity() * cal;
		}
		if (isAddMainCals) {
			cals += mainCals;
		}else if(mainCals != 0){
			//judge cals < 11.2%*TE
			int te = mainCals * 100 / 18;
			if(cals < te*11.2/100){
				cals = te*32/100 - cals;
			}
		}
		return (int) cals;
	}

	public static List<MealCourse> convertCourse(List<Course> list) {

		if (list == null)
			return new ArrayList<MealCourse>();

		ArrayList<MealCourse> res = new ArrayList<MealCourse>();

		for (Course c : list) {
			MealCourse mc = new MealCourse();
			mc.setCourse(c);
			mc.setQuantity(1);
			mc.setUnit(c.getUnit());
			res.add(mc);
		}
		return res;
	}

	public static List<Food> convertFruitCourse(List<MealCourse> list) {

		if (list == null)
			return new ArrayList<Food>();

		ArrayList<Food> res = new ArrayList<Food>();

		for (MealCourse c : list) {
			try {
				res.add(c.getCourse().getItems().get(0).getFood());
			} catch (RuntimeException re) {
				Log.e("Utils", "convertFruitCourse error:" + re.getMessage());
			}
		}
		return res;
	}

	public static <T> List<T> convertArray(T[] array) {
		List<T> list = new ArrayList<T>();
		if (array == null)
			return list;
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	public static <T> boolean listContains(T target, List<T> list) {
		if (list == null)
			return false;

		boolean flag = false;
		for (T org : list) {
			if (org.equals(target)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static <T> void listRemove(T target, List<T> list) {
		if (list == null)
			return;

		for (T org : list) {
			if (org.equals(target)) {
				list.remove(org);
				break;
			}
		}
	}

	public static <T> void listAdd(T target, List<T> list) {
		if (list == null)
			return;
		if (!listContains(target, list)) {
			list.add(target);
		}
	}

	public static void mergeCourseFood(CourseFood target, List<CourseFood> list) {
		if (Utils.listContains(target, list)) {
			for (CourseFood courseFood2 : list) {
				if (courseFood2.getFood().getId()
						.equals(target.getFood().getId())) {
					courseFood2.setQuantity(courseFood2.getQuantity()
							+ target.getQuantity());
					break;
				}
			}
		} else {
			list.add(target);
		}
	}

	public static void mergeCourseFood(List<CourseFood> target,
			List<CourseFood> list) {
		for (CourseFood courseFood : target) {
			mergeCourseFood(courseFood, list);

		}
	}

	/**
	 * 获取全部的菜品集合
	 * 
	 * @param courses
	 * @return
	 */
	public static List<CourseFood> getChildItem(List<Course> courses) {
		List<CourseFood> childItems = new ArrayList<CourseFood>();
		if (courses != null) {
			for (Course course : courses) {
				List<CourseFood> courseFoods = new ArrayList<CourseFood>();
				courseFoods = course.getItems();
				if (courseFoods != null) {
					for (CourseFood courseFood : courseFoods) {
						if (!CourseFood.FOODTYPE_PEILIAO.equals(courseFood
								.getFoodtype())) {
							Utils.mergeCourseFood(courseFood, childItems);
						}
					}
				}
			}

		}
		return childItems;
	}

	public static String getDivide(int divide, int divided) {
		divide = divide * 10;
		int ires = divide / divided;
		String res = "";// String.valueOf(ires);
		// if(res.length() < 2){
		// res = "0." +res.substring(res.length()-1) + "倍，评价:";
		// }else {
		// res = res.substring(0, res.length()-1) + "."
		// +res.substring(res.length()-1) + "倍，评价:";
		// }

		if (ires < 7) {
			res += "<span style=\"background-color:#f88855\"><font color=\"#f88855\">计划摄入热量太少!</font></span>";
		} else if (ires >= 7 && ires < 8) {
			res += "低于热量摄入标准.";
		} else if (ires >= 8 && ires < 12) {
			res += "完美符合热量摄入标准";
		} else if (ires >= 12 && ires < 13) {
			res += "高于热量摄入标准";
		} else {
			res += "<span style=\"background-color:#f88855\"><font color=\"#f88855\">计划摄入热量太多!</font></span>";
		}

		return res + "<BR>";
	}
	// 计算一天所有的菜
	public static List<Course> getAllCourse(Menu menu) {
		List<Course> courses = new ArrayList<Course>();
		courses.addAll((Utils.convertMealCourse(menu.getBreakfast().getItems())));
		courses.addAll((Utils.convertMealCourse(menu.getLunch().getItems())));
		courses.addAll((Utils.convertMealCourse(menu.getDinner().getItems())));
		return courses;
	}

	// 计算每餐饮食分类占的比列
	// public static List<Map<String, Integer>> getmealCoursecondScale(Menu
	// menu){
	// List<Map<String, Integer>> maps = new ArrayList<Map<String,Integer>>();
	// List<Course> courses = new ArrayList<Course>();
	// courses = getAllCourse(menu);
	// Map<String, Integer> map = new HashMap<String, Integer>();
	// for(int j = 0;j<TYPE.length;j++){
	// for (int i = 0; i < courses.size(); i++) {
	// int value = 0;
	// String coursecond = courses.get(i).getCoursecond();
	// if(coursecond.equals(TYPE[j])){
	// value = value+courses.get(i).getCalories();
	// map.put(TYPE[j],value);
	// }
	// }
	// }
	// maps.add(map);
	// return maps;
	// }
	public static OnClickListener delete_auto_btn(final AutoCompleteTextView tv) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv.setText(null);
			}
		};
	}

	public static void inputMethodManagerDismiss(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 保存搜索框历史记录
	 */
	public static void initAutoComplete(Context context, String filed,
			AutoCompleteTextView auto, String file_name) {
		SharedPreferences sp = context.getSharedPreferences(file_name, 0);
		String history = sp.getString(NoteActivity.HISTORY, "");
		final String[] historyArray = history.split(",");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_dropdown_item_1line, historyArray);
		if (historyArray.length > 10) {
			String[] newArray = new String[10];
			adapter = new ArrayAdapter<String>(context,
					android.R.layout.simple_dropdown_item_1line, newArray);
		}
		auto.setAdapter(adapter);
		// auto.setDropDownHeight(350);
		auto.setThreshold(1);

		auto.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				AutoCompleteTextView view = (AutoCompleteTextView) v;
				if (hasFocus) {
					view.showDropDown();
				}
			}
		});
		auto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AutoCompleteTextView view = (AutoCompleteTextView) v;

				view.showDropDown();

			}
		});
	}

	/**
	 * 将搜索框历史记录保存到对应的sharedpreference
	 */
	public static void saveAutoHistory(Context context, String field,
			AutoCompleteTextView auto, String file_name) {
		String text = auto.getText().toString();
		Log.i("tag", "text----" + text);
		SharedPreferences sp = context.getSharedPreferences(file_name, 0);
		String history = sp.getString(field, "");
		if (!history.contains(text + ",")) {
			StringBuilder sb = new StringBuilder(history);
			sb.insert(0, text + ",");
			sp.edit().putString(field, sb.toString()).commit();
		}

	}

	public static String removeLastStr(String str) {
		return str.substring(0, str.length() - 2);
	}
	public static String removeLast(String str) {
		return str.substring(7, str.length() - 2);
	}

	public static List<MealCourse> filterCourse(Meal meal) {
		List<MealCourse> filterMc = new ArrayList<MealCourse>();
		List<MealCourse> mealCourses = meal.getItems();
		for (MealCourse mealCourse : mealCourses) {
			String cond = mealCourse.getCourse().getCoursecond();
			if (cond.equals(COURSE_CONDITION_ZAODIAN)
					|| cond.equals(COURSE_CONDITION_ZHUSHI)) {
				filterMc.add(mealCourse);
			}
		}
		return filterMc;
	}

	public static View getConvertView(int size, LayoutInflater inflater,
			int position) {
		if (size > 1) {
			if (position == 0) {
				return inflater.inflate(R.layout.condition_lv_item_top, null);
			} else if (position == size - 1) {
				return inflater
						.inflate(R.layout.condition_lv_item_bottom, null);
			} else {
				return inflater.inflate(R.layout.condition_lv_item, null);
			}
		} else {
			return inflater.inflate(R.layout.condition_lv_item_single, null);
		}
	}

	public static long getDirSize(File dir) {
		long size = 0;
		if (dir.isFile()) {

			size = dir.length();
		} else {
			File[] subFiles = dir.listFiles();
			if(subFiles == null) return 0;
			for (File file : subFiles) {
				if (file.isFile()) {
					size += file.length();
				} else {
					size += getDirSize(file);
				}

			}
		}
		return size;
	}
	public static WeakReference<Bitmap> getBitmap(Context context,int id){
		try{
		InputStream is = context.getResources().openRawResource(id);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap btp = BitmapFactory.decodeStream(is,null,options);
		WeakReference<Bitmap> sb = new WeakReference<Bitmap>(btp);
		btp = null;
		return sb;
		}catch(RuntimeException re){
    		Log.e(TAG, "RuntimeException");
    	}catch(OutOfMemoryError oe){
    		Log.e(TAG, "OutOfMemoryError");
    	}
		return null;
	}
	public static SoftReference<Bitmap> getSoftBitmap(Context context,int id){
		try{
		InputStream is = context.getResources().openRawResource(id);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		Bitmap btp = BitmapFactory.decodeStream(is,null,options);
		SoftReference<Bitmap> sb = new SoftReference<Bitmap>(btp);
		btp = null;
		return sb;
		}catch(RuntimeException re){
    		Log.e(TAG, "RuntimeException");
    	}catch(OutOfMemoryError oe){
    		Log.e(TAG, "OutOfMemoryError");
    	}
		return null;
	}
	public static void addImageView(final Context context,final LinearLayout layout,int id1,final int id2,final String key,final SharedPreferences sp){
		try{
		final ImageView iv = new ImageView(context);	
		final WeakReference<Bitmap> bitmap = Utils.getBitmap(context,id1);
		iv.setImageBitmap(bitmap.get());
		iv.setTag(1);
		iv.setScaleType(ScaleType.FIT_XY);
		iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		iv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				int tag = (Integer) v.getTag();
				if(id2 != 0){
					if(tag==1){
					
						iv.setImageBitmap(Utils.getBitmap(context,id2).get());
						iv.setTag(2);
				    }else{
				    	sp.edit().putBoolean(key, !sp.getBoolean(key, true)).commit();
				    	layout.removeAllViews();
				    }
				}else{
					sp.edit().putBoolean(key, !sp.getBoolean(key, true)).commit();
					
					layout.removeAllViews();
				}
			}
		});
		layout.addView(iv);
		}catch(RuntimeException re){
			Log.i("tag", "RuntimeException");
    	}catch(OutOfMemoryError oe){
    		Log.i("tag", "OutOfMemoryError");
    	}
	}
}
