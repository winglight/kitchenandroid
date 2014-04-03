package com.syt.health.kitchen;

import java.util.ArrayList;
import java.util.List;
import com.syt.health.kitchen.db.RestaurantModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.utils.Utils;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
public class HistoryActivity extends BaseActivity {
	private ServiceImpl service;
	private ListView hotel_lv;
	private List<RestaurantModel> restaurantModels;
	private Button edit_btn;
	private boolean flag = false;
	private MyListAdapter adapter;
	private List<String>comments = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_history);
		init();
	}
	@Override
	public void onResume() {
		super.onResume();
		service = getService();
		restaurantModels = service.getCurrentUser().getObjOutsideHistoryList();
		if(restaurantModels!= null){
			adapter = new MyListAdapter();
			hotel_lv.setAdapter(adapter);
			hotel_lv.setOnItemClickListener(hotel_lv_listener);
		}
	}
	private void init(){
		hotel_lv = (ListView)findViewById(R.id.activity_history_listview);
		edit_btn = (Button)findViewById(R.id.activity_history_edit_button);
		edit_btn.setOnClickListener(edit_btn_listener);
	}
	private OnClickListener edit_btn_listener = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			if(flag){
				edit_btn.setText(HistoryActivity.this.getResources().getString(R.string.mealtimesinfo_edit));
				flag = false;
				adapter.notifyDataSetChanged();
			}else{
				edit_btn.setText(HistoryActivity.this.getResources().getString(R.string.mealtimesinfo_edit_ok));
				flag = true;
				adapter.notifyDataSetChanged();
			}
		}
	};
	private OnItemClickListener hotel_lv_listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {			
			Intent intent = new Intent();
			intent.putExtra("restaurantModel",restaurantModels.get(arg2));
			intent.setClass(HistoryActivity.this, CourseHistoryActivity.class);
			startActivity(intent);
		}
	};
	private OnClickListener comment_btn_listener = new OnClickListener() {		
		@Override
		public void onClick(final View v) {		
			if(flag){
				new AlertDialog.Builder(HistoryActivity.this).setTitle(HistoryActivity.this.getResources().getString(R.string.dialog_title)).
				setMessage(HistoryActivity.this.getResources().getString(R.string.dialog_delete)).
				setPositiveButton(HistoryActivity.this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RestaurantModel restaurantModel = restaurantModels.get((Integer)v.getTag());
						restaurantModels.remove(restaurantModel);
						service.removeOutsideHistoryList(restaurantModel);
						adapter.notifyDataSetChanged();
					}
				}).setNegativeButton(HistoryActivity.this.getResources().getString(R.string.cancel), null).show();
			}else{
				commentDialogShow((Integer) v.getTag());
			}
		}
	};
	private AlertDialog commentDialog(final int position){
		return new AlertDialog.Builder(this).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
					restaurantModels.get(position).setComment(Utils.arrayIntoString(comments));
					service.saveOutsideHistoryList(restaurantModels.get(position));
					adapter.notifyDataSetChanged();
			}
		}).setNegativeButton(getResources().getString(R.string.cancel), null).create();
	}
	private OnCheckedChangeListener checkbox_listener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			String text = buttonView.getText().toString();
			if(isChecked){
				comments.add(text);
			}else{
				comments.remove(text);
			}
		}
	};
	private void commentDialogShow(int position){
		comments.clear();
		AlertDialog dialog  = commentDialog(position);
		String[]comments = getResources().getStringArray(R.array.comment);
		LinearLayout view = new LinearLayout(this);
		view.setOrientation(LinearLayout.HORIZONTAL);	
		LinearLayout child = null;
		for (int i = 0; i < comments.length; i++) {
			CheckBox cb = new CheckBox(this);
			cb.setText(comments[i]);
			if(i%3==0){
				child = new LinearLayout(this);
				child.setOrientation(LinearLayout.VERTICAL);
				view.addView(child);
			}
			cb.setOnCheckedChangeListener(checkbox_listener);
			child.addView(cb);
		}
		dialog.setView(view);
		dialog.show();
	}
	class MyListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return restaurantModels.size();
		}

		@Override
		public Object getItem(int position) {
			return restaurantModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if(convertView == null){
				holder = new Holder();
				convertView = LayoutInflater.from(HistoryActivity.this).inflate(R.layout.activity_history_listview_item, null);
				holder.name_tv = (TextView)convertView.findViewById(R.id.activity_history_listview_item_name);
				holder.pj_tv = (TextView)convertView.findViewById(R.id.activity_history_listview_item_pj);
				holder.comment_button = (Button)convertView.findViewById(R.id.activity_history_listview_item_btn);
				convertView.setTag(holder);
			}else{
				holder = (Holder)convertView.getTag();
			}
			if(flag){
				holder.comment_button.setText(HistoryActivity.this.getResources().getString(R.string.delete));
			}else{
				holder.comment_button.setText(HistoryActivity.this.getResources().getString(R.string.pingjia));
			}
			holder.comment_button.setOnClickListener(comment_btn_listener);
			holder.comment_button.setTag(position);
			holder.name_tv.setText(restaurantModels.get(position).getName());
			String conmment = restaurantModels.get(position).getComment();
			if(conmment!=null){
				if(conmment.length()!=0){
					holder.pj_tv.setText(conmment);
				}else{
					holder.pj_tv.setText("您还未给出任何评价");
				}
			}else{
				holder.pj_tv.setText("您还未给出任何评价");
			}
			return convertView;
		}	
	}
	static class Holder{
		TextView name_tv;
		TextView pj_tv;
		Button comment_button;
	}
}
