package com.syt.health.kitchen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.utils.DeliverData;
import com.syt.health.kitchen.utils.Utils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class BuyingActivity extends BaseActivity {
	private Button reset_btn;
	private ServiceImpl service;
	private ListView food_lv;
	private List<CourseFood>courseFoods = new ArrayList<CourseFood>();
	private ListAdapter adapter;
	private Context mContext;
	@Override     
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);   
		mContext = BuyingActivity.this;
		setContentView(R.layout.activity_buyinglist);
		service = getService();
		courseFoods = service.getCurrentUser().getObjFoodList();
		//courseFoods = getCourseFoods();		
		init();
		if(courseFoods!=null &&courseFoods.size()!=0){
			initializeData();
		}else{
			new AlertDialog.Builder(this).setTitle(this.getResources().getString(R.string.dialog_title)).
			setMessage("您还未选择您需要购买的食材，是否跳到选择界面？").
			setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(BuyingActivity.this, ResetListActivity.class);
					startActivity(intent);
				}
			}).show();
		}
	}  
	private void init(){
		reset_btn = (Button)findViewById(R.id.activity_buyinglist_reset_btn);
		reset_btn.setOnClickListener(reset_btn_listener);
		food_lv = (ListView)findViewById(R.id.activity_buyinglist_food_listview);
	}
	private void initializeData(){
		adapter = new ListAdapter();
		food_lv.setAdapter(adapter);
		food_lv.setOnItemClickListener(lv_listener);
	}
	private OnClickListener reset_btn_listener = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(BuyingActivity.this, ResetListActivity.class);
			startActivity(intent);
		}
	};
	private OnItemClickListener lv_listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			CourseFood courseFood = courseFoods.get(arg2);
			Intent intent = new Intent();
			intent.putExtra("courseFood", courseFood);
			intent.setClass(BuyingActivity.this, FoodInfoActivity.class);
			startActivity(intent);
		}
	};
	class ListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return courseFoods.size();
		}
		@Override
		public CourseFood getItem(int arg0) {
			return courseFoods.get(arg0);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder h;
			if(convertView == null){
				 h = new Holder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.buyingactivity_list_item, null);
				h.tv = (TextView)convertView.findViewById(R.id.buyingactivity_list_item_tv);
				h.btn = (Button)convertView.findViewById(R.id.buyingactivity_list_item_button);
				h.view = (View)convertView.findViewById(R.id.buyingactivity_view);
				convertView.setTag(h);
			}else{
				h = (Holder)convertView.getTag();
			}
			CourseFood courseFood = courseFoods.get(position);
			h.tv.setText(courseFood.getFood().getName()+"x"+courseFood.getQuantity()+courseFood.getFoodunit());
			LayoutParams params = (LayoutParams) h.view.getLayoutParams();
			params.width = h.tv.getWidth();
			params.height = 4;
			 h.view.setLayoutParams(params);
			if(courseFood.isFlag()){
				h.btn.setText(mContext.getResources().getString(R.string.yigou));
				h.view.setVisibility(View.VISIBLE);
			}else{
				h.btn.setText(mContext.getResources().getString(R.string.weigou));
				h.view.setVisibility(View.GONE);
			}
			h.btn.setOnClickListener(button_listener);
			h.btn.setTag(position);
			return convertView;
		}	
	}
	private OnClickListener button_listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {			
			int position = (Integer) v.getTag();
			if(courseFoods.get(position).isFlag()){
				courseFoods.get(position).setFlag(false);
			}else{
				courseFoods.get(position).setFlag(true);
			}
			adapter.notifyDataSetChanged();
		}
	};
	static class Holder{
		TextView tv;
		Button btn;
		View view;
	}
}
