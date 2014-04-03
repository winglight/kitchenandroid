package com.syt.health.kitchen;

import java.util.ArrayList;
import java.util.List;
import com.syt.health.kitchen.db.RestaurantModel;
import com.syt.health.kitchen.json.Course;
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
public class CourseHistoryActivity extends BaseActivity {
	private ServiceImpl service;
	private ListView course_lv;
	private Button edit_btn;
	private boolean flag = false;
	private MyListAdapter adapter;
	private List<String>comments = new ArrayList<String>();
	private List<Course> courses = new ArrayList<Course>();
	private RestaurantModel restaurantModel;
	private TextView title_tv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_coursehistory);
		Intent intent = getIntent();
		restaurantModel = (RestaurantModel)intent.getSerializableExtra("restaurantModel");
		courses = restaurantModel.getCourseList();
		init();
	}
	private void init(){ 
		title_tv = (TextView)findViewById(R.id.activity_coursehistory_title_tv);
		title_tv.setText(restaurantModel.getName());
		course_lv = (ListView)findViewById(R.id.activity_coursehistory_listview);
		edit_btn = (Button)findViewById(R.id.activity_coursehistory_edit_button);
		edit_btn.setOnClickListener(edit_btn_listener);
		if(courses!= null){
			adapter = new MyListAdapter();
			course_lv.setAdapter(adapter);
		}
	}
	private OnClickListener edit_btn_listener = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			if(flag){
				edit_btn.setText(CourseHistoryActivity.this.getResources().getString(R.string.mealtimesinfo_edit));
				flag = false;
				adapter.notifyDataSetChanged();
			}else{
				edit_btn.setText(CourseHistoryActivity.this.getResources().getString(R.string.mealtimesinfo_edit_ok));
				flag = true;
				adapter.notifyDataSetChanged();
			}
		}
	};
	private OnClickListener comment_btn_listener = new OnClickListener() {		
		@Override
		public void onClick(final View v) {		
			if(flag){
				new AlertDialog.Builder(CourseHistoryActivity.this).setTitle(CourseHistoryActivity.this.getResources().getString(R.string.dialog_title)).
				setMessage(CourseHistoryActivity.this.getResources().getString(R.string.dialog_delete)).
				setPositiveButton(CourseHistoryActivity.this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Course course = courses.get((Integer)v.getTag());
						courses.remove(course);
						restaurantModel.setCourseList(courses);
						service.removeOutsideHistoryList(restaurantModel);
						adapter.notifyDataSetChanged();
					}
				}).setNegativeButton(CourseHistoryActivity.this.getResources().getString(R.string.cancel), null).show();
			}else{
				commentDialogShow((Integer) v.getTag());
			}
		}
	};
	private AlertDialog commentDialog(final int position){
		return new AlertDialog.Builder(this).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {		
			@Override
			public void onClick(DialogInterface dialog, int which) {
			        courses.get(position).setComment(Utils.arrayIntoString(comments));
			        restaurantModel.setCourseList(courses);
					service.saveOutsideHistoryList(restaurantModel);
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
			return courses.size();
		}

		@Override
		public Object getItem(int position) {
			return courses.get(position);
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
				convertView = LayoutInflater.from(CourseHistoryActivity.this).inflate(R.layout.activity_history_listview_item, null);
				holder.name_tv = (TextView)convertView.findViewById(R.id.activity_history_listview_item_name);
				holder.pj_tv = (TextView)convertView.findViewById(R.id.activity_history_listview_item_pj);
				holder.comment_button = (Button)convertView.findViewById(R.id.activity_history_listview_item_btn);
				convertView.setTag(holder);
			}else{
				holder = (Holder)convertView.getTag();
			}
			if(flag){
				holder.comment_button.setText(CourseHistoryActivity.this.getResources().getString(R.string.delete));
			}else{
				holder.comment_button.setText(CourseHistoryActivity.this.getResources().getString(R.string.pingjia));
			}
			holder.comment_button.setOnClickListener(comment_btn_listener);
			holder.comment_button.setTag(position);
			holder.name_tv.setText(courses.get(position).getName());
			String conmment = courses.get(position).getComment();
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
