package com.syt.health.kitchen.fragment;

import java.io.Serializable;
import java.util.List;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SettingsActivity;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.Calculate;
import com.syt.health.kitchen.utils.Utils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MealTimesInfoFragment extends Fragment {
	public final static String TAG = "MealTimesInfoFragment";
	private Menu menu;
	private Meal meal = null;  
	private ListView food_lv;
	private Button intelligent_btn;
	private Button add_btn;
	private ServiceImpl service;
	private TextView heattotal_tv;
	private TextView title_tv;
	private Button back_btn;
	private Button edit_btn;
	private MealTimesInfoListAdapter lv_adapter;
	private List<MealCourse> courses;
	private int type;
	private ProgressDialog pd;
	public static boolean b = false;
	//未完成界面刷新
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		menu = (Menu) getArguments().getSerializable("menu");
		meal = (Meal)getArguments().getSerializable("meal");
		courses = Utils.getMealCourses(meal.getItems());   
		service = ((BaseActivity)getActivity()).getService();
	}
//	@Override
//	public void onResume() {
//		super.onResume();
//		service.fetchMealsByDate(new TaskCallBack<String, MessageModel<Menu>>() {			
//			@Override
//			public void postExecute(MessageModel<Menu> result) {				
//				if(result.isFlag()){
//					meal = result.getData().getMealByType(type);
//					initializeData(meal);
//				}
//			}
//			
//			@Override
//			public String getParameters() {
//				return menu.getMenudate();
//			}
//		});
//	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_mealtimesinfo, container,false);  
		init(view);
		return view;
	}
	/**
	 *  initialize UI
	 * @param view
	 * @author Stefan
	 */
	private void init(View view){
	    title_tv = (TextView)view.findViewById(R.id.fragment_mealtimesinfo_title_textview);
		heattotal_tv = (TextView)view.findViewById(R.id.fragment_mealtimesinfo_heattotal_tv);	
		food_lv = (ListView)view.findViewById(R.id.fragment_mealtimesinfo_listview);
		intelligent_btn = (Button)view.findViewById(R.id.fragment_mealtimesinfo_intelligence_btn);
		add_btn = (Button)view.findViewById(R.id.fragment_mealtimesinfo_addfood_btn);
		intelligent_btn.setOnClickListener(btn_listener);
		add_btn.setOnClickListener(btn_listener);
		back_btn = (Button)view.findViewById(R.id.fragment_mealtimesinfo_back_btn);
		back_btn.setOnClickListener(btn_listener);
		edit_btn = (Button)view.findViewById(R.id.fragment_mealtimesinfo_edit_button);   
		lv_adapter = new MealTimesInfoListAdapter();
		food_lv.setAdapter(lv_adapter);
		initializeData(meal);
		edit_btn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(b==false){
					lv_adapter.setVisibility(View.VISIBLE);
					lv_adapter.notifyDataSetInvalidated();
					edit_btn.setText(getActivity().getResources().getString(R.string.mealtimesinfo_edit_ok));
					b = true;
				}else{
					lv_adapter.setVisibility(View.GONE);
					lv_adapter.notifyDataSetInvalidated();
					edit_btn.setText(getActivity().getResources().getString(R.string.mealtimesinfo_edit));
					b =false;
				}
			}
		});
	}
	private void initializeData(Meal meal){
		type = meal.getType(); 
		if(type ==2 ){
			title_tv.setText(menu.getMenudate()+getActivity().getResources().getString(R.string.lunch)+"");
		}else if(type==3){
			title_tv.setText(menu.getMenudate()+getActivity().getResources().getString(R.string.dinner)+"");
		}
		if(meal.getItems()!=null){
			if(meal.getItems().length!=0){
				int heat = Calculate.calculateTotalHet(meal.getItems());
				heattotal_tv.setText(heat+getActivity().getResources().getString(R.string.caluli)+"，"+Calculate.calcuatePerson(heat, 500)+"人");
				food_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						Course course = lv_adapter.getItem(arg2).getCourse();
						if(course!=null){
							((BaseActivity)getActivity()).addFragment(FoodInfoFragment.newInstance(course), FoodInfoFragment.TAG);
						}
					}
				});
		    }
		}
	}
	/**
	 * Binding data for MealTimesInfoFragment
	 * @param menu
	 * @param meal
	 * @return mealTimesInfoFragment
	 */
	public static MealTimesInfoFragment newInstance(Menu menu,Meal meal){
		MealTimesInfoFragment fragment = new MealTimesInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("menu", menu);
		bundle.putSerializable("meal",meal);
		fragment.setArguments(bundle);
		return fragment;
	}
	private OnClickListener btn_listener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Button button = (Button)v;
			switch (button.getId()) {
			case R.id.fragment_mealtimesinfo_back_btn:
				((BaseActivity)getActivity()).backFragment(MealTimesInfoFragment.this);
				break;
			case R.id.fragment_mealtimesinfo_intelligence_btn:
				new AlertDialog.Builder(getActivity()).setTitle(getActivity().getResources().
						getString(R.string.dialog_title)).
						setMessage(getActivity().getResources().getString(R.string.prompt_01)).
				setPositiveButton(getActivity().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pd = ProgressDialog.show(getActivity(), "", "正在智能生成菜谱，请稍后。");
						((BaseActivity)getActivity()).getService().generateMealByHealthCondition(menu, new TaskCallBack<Meal, MessageModel<Meal>>() {							
							@Override
							public void postExecute(MessageModel<Meal> result) {
								if(result.isFlag()){
									meal = result.getData();
									meal.setType(2);
									initializeData(meal);
									courses = Utils.getMealCourses(meal.getItems()); 
									lv_adapter.notifyDataSetChanged();								
									}else{
									if(result.getErrorCode() == 2001){
										new AlertDialog.Builder(getActivity()).setTitle(getActivity().getResources().
												getString(R.string.dialog_title)).
												setMessage(getActivity().getResources().getString(R.string.prompt_02)).
										setPositiveButton(getActivity().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												Intent intent = new Intent(getActivity(), SettingsActivity.class);
												getActivity().startActivity(intent);
											}
										}).setNegativeButton(getActivity().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
													int which) {										
											}						
										}).show();
									}
								}
								pd.dismiss();
							}							
							@Override
							public Meal getParameters() {
								return meal;
							}
						});
					}
				}).setNegativeButton(getActivity().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {										
					}						
				}).show();
				break;
			case R.id.fragment_mealtimesinfo_addfood_btn:
				((BaseActivity)getActivity()).addFragment(AddCourseFragment.newInstance(menu,meal), AddCourseFragment.TAG);
				break;
			}
		}
	};
	public class MealTimesInfoListAdapter extends BaseAdapter{
		int visibility = View.GONE;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return courses.size();
		}

		@Override
		public MealCourse getItem(int position) {
			// TODO Auto-generated method stub
			return courses.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		public int getVisibility() {
			return visibility;
		}
		public void setVisibility(int visibility) {
			this.visibility = visibility;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			if(convertView == null){
				holder = new Holder();
				convertView = inflater.inflate(R.layout.fragment_mealtimesinfo_list_item, null);
				holder.name_tv =(TextView)convertView.findViewById(R.id.fragment_mealtimesinfo_list_name_tv);
				holder.count_tv = (TextView)convertView.findViewById(R.id.fragment_mealtimesinfo_list_count_tv);
				holder.suitableObject_tv = (TextView)convertView.findViewById(R.id.fragment_mealtimesinfo_list_suitableObject_tv);
				holder.unsuitableObject_tv = (TextView)convertView.findViewById(R.id.fragment_mealtimesinfo_list_unsuitableObject_tv);
				holder.linearLayout = (LinearLayout)convertView.findViewById(R.id.fragment_mealtimesinfo_list_linear);
				holder.add_btn = (Button)convertView.findViewById(R.id.fragment_mealtimesinfo_list_add_btn);
				holder.minus_btn = (Button)convertView.findViewById(R.id.fragment_mealtimesinfo_list_minus_btn);
				holder.delet_btn=(Button)convertView.findViewById(R.id.fragment_mealtimesinfo_list_delete_btn);
				convertView.setTag(holder);
			}else{
				holder = (Holder)convertView.getTag();
			}
			holder.linearLayout.setVisibility(visibility);
			MealCourse mealCourse = courses.get(position);
			holder.name_tv.setText(mealCourse.getCourse().getName()+"：");
			holder.count_tv.setText(mealCourse.getQuantity()+"*"+mealCourse.getCourse().getUnit()
					+","+mealCourse.getCourse().getCalories()*mealCourse.getQuantity()+getActivity().getResources().getString(R.string.caluli));
			holder.suitableObject_tv.setText(Utils.arrayIntoString(mealCourse.getCourse().getEffectivity()));
			holder.unsuitableObject_tv.setText(Utils.arrayIntoString(mealCourse.getCourse().getIncompatible()));
			holder.add_btn.setOnClickListener(add_btn_listener);
			holder.add_btn.setTag(position);
			holder.minus_btn.setOnClickListener(minus_btn_listener);
			holder.minus_btn.setTag(position);
			holder.delet_btn.setOnClickListener(delete_btn_listener);
			holder.delet_btn.setTag(position);
			return convertView;
		}		
	}
	public static class Holder{
	    TextView name_tv;
		TextView count_tv;
		TextView suitableObject_tv;
		TextView unsuitableObject_tv;
		LinearLayout linearLayout;
		Button add_btn;
		Button minus_btn;
		Button delet_btn;
	}
	private OnClickListener add_btn_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {			  
			int position=  (Integer) v.getTag();
			MealCourse mealCourse =meal.getItems()[position];
			mealCourse.setQuantity(mealCourse.getQuantity()+1);
			initializeData(meal);
			lv_adapter.notifyDataSetChanged();  
		}
	};
	private OnClickListener minus_btn_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			int position=  (Integer) v.getTag();
			MealCourse mealCourse =meal.getItems()[position];
			if(mealCourse.getQuantity()<1){
				Toast.makeText(getActivity(), "数量不能为负数", Toast.LENGTH_LONG).show();
			}else{
				mealCourse.setQuantity(mealCourse.getQuantity()-1);
				initializeData(meal);
				lv_adapter.notifyDataSetChanged();  
			}
		}
	};
	private OnClickListener delete_btn_listener = new OnClickListener() {	 
		@Override
		public void onClick(final View v) {		
			new AlertDialog.Builder(getActivity()).setTitle(getActivity().getResources().getString(R.string.dialog_title)).
			setMessage(getActivity().getResources().getString(R.string.dialog_delete)).
			setPositiveButton(getActivity().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int position=  (Integer) v.getTag();
					MealCourse mealCourse =courses.get(position);
					courses.remove(mealCourse);
					lv_adapter.notifyDataSetChanged();
					initializeData(meal);
				}
			}).setNegativeButton(getActivity().getResources().getString(R.string.cancel), null).show();
		}
	};
}
