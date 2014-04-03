package com.syt.health.kitchen;

import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.HealthConditionModel;
import com.syt.health.kitchen.fragment.BuyEditorFragment;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.Utils;
import com.syt.health.kitchen.widget.ClassifyWheelAdapter;
import com.syt.health.kitchen.widget.ConditionWheelAdapter;
import com.syt.health.kitchen.widget.OnWheelChangedListener;
import com.syt.health.kitchen.widget.WheelView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SetConditionActivity extends BaseActivity {
	private WheelView wv_classifyCondition;
	private WheelView wv_condition;
	private Button people_btn;
	private List<HealthCondClassifyModel> classifyModels;
	private ServiceImpl service;
	private ListView condition_lv;
	private List<String> conditions= new ArrayList<String>();;
	private ConditionListAdapter adapter;
	private GenerateCondition generateCondition;
	private static final int MAX = 4;
	private String date;
	private int numPeople = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setcondition);
		service = getService();
		classifyModels = service.getAllHealthCondition();
		if(service.getCurrentMenu() != null && service.getCurrentMenu().getSmartParams() != null){
		generateCondition = service.getCurrentMenu().getSmartParams().clone();
		}else{
			generateCondition = service.getCurrentUser().getObjSmartParams();
		}
		numPeople = generateCondition.getPeople();
		conditions = generateCondition.getHealthcondition();
		date = getIntent().getStringExtra(Utils.DATE_KEY);
		
		init();
	}
	private void init(){
		SharedPreferences sp =  getSharedPreferences("sp_first",0);
		LinearLayout help_layout = (LinearLayout)findViewById(R.id.activity_setcondition_help_layout);
		if(sp.getBoolean(Utils.SETCONDITION_FLAG, true)){
			Utils.addImageView(this, help_layout, R.drawable.help_set_condition, 0, Utils.SETCONDITION_FLAG, sp);
			sp.edit().putBoolean(Utils.NOTE_FLAG_02, true).commit();
		}
		if(conditions==null){
			conditions = new ArrayList<String>();
		}
		wv_classifyCondition = (WheelView)findViewById(R.id.activity_setcondition_type_wheel_view);
		wv_condition = (WheelView)findViewById(R.id.activity_setcondition_condition_wheel_view);
		wv_condition.setAdapter(new ConditionWheelAdapter(classifyModels.get(0).getSubList()));
		wv_condition.setVisibleItems(5);
		wv_classifyCondition.setAdapter(new ClassifyWheelAdapter(classifyModels));
		wv_classifyCondition.addChangingListener(new OnWheelChangedListener() {			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wv_condition.setAdapter(new ConditionWheelAdapter(classifyModels.get(newValue).getSubList()));
				wv_condition.setCurrentItem(0);
			}
		});
		wv_classifyCondition.setCurrentItem(0);
		
		condition_lv = (ListView)findViewById(R.id.activity_setcondition_condition_lv);
	    adapter =new ConditionListAdapter();
		condition_lv.setAdapter(adapter);
		
		people_btn = (Button)findViewById(R.id.activity_setcondition_people_tv);
		people_btn.setText(numPeople+"");
		
		Button add_btn = (Button)findViewById(R.id.activity_setcondition_add_course_btn);
		add_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {


				String selCondition = classifyModels.get(wv_classifyCondition.getCurrentItem()).
						getSubList().get(wv_condition.getCurrentItem()).getName();
				
				if(!Utils.listContains(selCondition, conditions)){
					if(conditions.size()<=MAX){
						conditions.add(selCondition);
						adapter.notifyDataSetChanged();
					}else{
						new AlertDialog.Builder(SetConditionActivity.this).
						setMessage(getResources().getString(R.string.sethealth_dialog)).
						setPositiveButton(getResources().getString(R.string.know_dialog), new DialogInterface.OnClickListener() {			
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						}).show();
					}
				}else{
					Toast.makeText(SetConditionActivity.this,getResources().getString(R.string.exist_healthcondition), Toast.LENGTH_SHORT).show();
				}
			
			}
		});		
		
		Button save_btn = (Button)findViewById(R.id.activity_setcondition_save_btn);
		save_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {

					if(Utils.listContains((Utils.YOU_ER), conditions)){
						if(numPeople!=1){
						new AlertDialog.Builder(SetConditionActivity.this).setTitle(getResources().getString(R.string.dialog_title)).
						setMessage(getResources().getString(R.string.includ_baby_yesORno)).
						setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {			
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
									numPeople--;	
									people_btn.setText(numPeople+"");
									createMenu();	
								   
							}
						}).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								createMenu();
							}
						}).show();
			    	}else{
			    		createMenu();
			    	}				
				}else{
					createMenu();
				}
					
			}
		});
		
		final Button up_btn = (Button)findViewById(R.id.activity_setcondition_up_btn);
		final Button next_btn = (Button)findViewById(R.id.activity_setcondition_next_btn);
		up_btn.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(numPeople!=1){
					up_btn.setBackgroundResource(R.drawable.date_up_button);
					next_btn.setBackgroundResource(R.drawable.date_next_button);
					numPeople--;
				}else{
					up_btn.setBackgroundResource(R.drawable.date_up_button_bg_disable);
				}
				people_btn.setText(numPeople+"");
			}
		});
	
		next_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(numPeople!=10){
					next_btn.setBackgroundResource(R.drawable.date_next_button);
					up_btn.setBackgroundResource(R.drawable.date_up_button);
					numPeople++;
				}else{
					next_btn.setBackgroundResource(R.drawable.date_next_button_bg_disable);
				}
				people_btn.setText(numPeople+"");
			}
		});
	}	
	class ConditionListAdapter extends BaseAdapter{
		@Override
		public int getCount() {
		
			return  conditions.size();
		}
		@Override
		public Object getItem(int position) {
			
			return conditions.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder h;
			LayoutInflater inflater = LayoutInflater.from(SetConditionActivity.this);
			h = new Holder();
			convertView = Utils.getConvertView(conditions.size(), inflater, position);
	//		convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,(int)getResources().getDimension(R.dimen.list_item_height)));
			h.condition_tv = (TextView)convertView.findViewById(R.id.condition_lv_item_name_tv);
			h.delete_btn = (TextView)convertView.findViewById(R.id.condition_lv_item_delete_btn);			
			h.condition_tv.setText(conditions.get(position));
			
			h.delete_btn.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
					conditions.remove(position);
					adapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}		
	}
	class Holder{
		TextView condition_tv;
		TextView delete_btn;
	}
	private void generateMenu(final String date) {
		final ProgressDialog pd = ProgressDialog.show(SetConditionActivity.this, null,
				null);
		
		service.generateMenuByHealthCondition(date,
				new TaskCallBack<String, MessageModel<Menu>>() {
					@Override
					public void postExecute(MessageModel<Menu> result) {
						if (result.isFlag()) {
							finish();
						} else {
							Toast.makeText(SetConditionActivity.this,
									result.getMessage(),
									Toast.LENGTH_LONG).show();
						}
						if(pd.isShowing()){
						pd.dismiss();
						}
					}

					@Override
					public String getParameters() {
						return date;
					}
				});
	}
	private void createMenu(){
		generateCondition.setHealthcondition(conditions);
		numPeople = Integer.parseInt(people_btn.getText().toString());
		generateCondition.setPeople(numPeople);
		boolean backFlag = service.saveSmartParams(generateCondition);
		Log.i("tag", "backFlag="+backFlag);
		if(backFlag){
			generateMenu(date);
		}else{
			finish();
		}		
	}
}
