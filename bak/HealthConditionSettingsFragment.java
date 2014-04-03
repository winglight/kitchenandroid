package com.syt.health.kitchen.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.SettingsActivity;
import com.syt.health.kitchen.R.id;
import com.syt.health.kitchen.R.layout;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.HealthConditionModel;
import com.syt.health.kitchen.service.param.SmartMealConditionParam;
import com.syt.health.kitchen.utils.Utils;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HealthConditionSettingsFragment extends Fragment{
	private static final String TAG = "HealthConditionSettingsFragment";
	private static String PARAMS_DATA_EXTRA = "params";
	
	private SmartMealConditionParam params;
	private EditText peopleAmountTxt;
	private ExpandableListView requirementList;
	private List<Integer> selConditionList;
	private List<String> selConditionListStr;
	private boolean isHome;

	public static HealthConditionSettingsFragment newInstance(SmartMealConditionParam params, boolean isHome){
		final HealthConditionSettingsFragment f = new HealthConditionSettingsFragment();		
		final Bundle args = new Bundle();
		args.putBoolean(SettingsActivity.IS_HOME_EXTRA_DATA, isHome);
        args.putSerializable(PARAMS_DATA_EXTRA, params);
        f.setArguments(args);	
		return f;
	}
    /**
     * Empty constructor as per the Fragment documentation
     */
    public HealthConditionSettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        params =  (getArguments() != null ? (SmartMealConditionParam)getArguments().getSerializable(PARAMS_DATA_EXTRA) : null);
        isHome = getArguments() != null ? getArguments().getBoolean(SettingsActivity.IS_HOME_EXTRA_DATA) : true; 
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_health_condition_settings, container, false);
 		peopleAmountTxt = (EditText) v.findViewById(R.id.peopleAmountTxt);
 		requirementList = (ExpandableListView) v.findViewById(R.id.healthRequirementList);
 		
 		if(params != null){
 			peopleAmountTxt.setText(String.valueOf(params.getPeople()));
 			selConditionList = Arrays.asList(params.getHealthcondition());
 			selConditionListStr = Arrays.asList(params.getHealthconditionStr());
 		}else{
 			peopleAmountTxt.setText("1");
 			selConditionList = new ArrayList<Integer>();
 			selConditionListStr = new ArrayList<String>();
 		}
 		
 		initList();
 		
 		Button saveBtn = (Button) v.findViewById(R.id.saveConditionBtn);
 		if(isHome){
 			saveBtn.setText(R.string.save_condition);
 		}else{
 			saveBtn.setText(R.string.start_choose_courses);
 		}
 		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(params == null){
					params = new SmartMealConditionParam();
				}
				params.setPeople(Integer.valueOf(peopleAmountTxt.getText().toString()));
				params.setHealthcondition(selConditionList.toArray(new Integer[]{}));
				params.setHealthconditionStr(selConditionListStr.toArray(new String[]{}));
				
				((SettingsActivity)getActivity()).saveParams(params);
			}
		});
 		
        return v;
    }
    
    private void initList(){
//    	requirementList.setItemsCanFocus(false);
//    	requirementList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//    	requirementList.setGroupIndicator(null);
//    	requirementList.setDivider(null);
    	
    	//load data from db
    	final List<HealthCondClassifyModel> conditionList = ((SettingsActivity)getActivity()).getConditionList();
    	conditionList.remove(0);
    	MyExpandableListAdapter adapter = new MyExpandableListAdapter(conditionList);
    	requirementList.setAdapter(adapter);
    	
    	requirementList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				HealthConditionModel condition = conditionList.get(groupPosition).getSubList().get(childPosition);
	            if(Utils.listContains(condition.getId(), selConditionList)){
	            	Utils.listRemove(condition.getId(), selConditionList);
	            	Utils.listRemove(condition.getName(), selConditionListStr);
	            	((CheckBox)v).setChecked(false);
	            }else{
	            	Utils.listAdd(condition.getId(), selConditionList);
	            	Utils.listAdd(condition.getName(), selConditionListStr);
	            	((CheckBox)v).setChecked(true);
	            }
	            
	            return true;
			}
		});
    }
    
    public class MyExpandableListAdapter extends BaseExpandableListAdapter{
        private List<HealthCondClassifyModel> conditionList;
        
        public MyExpandableListAdapter(List<HealthCondClassifyModel> conditionList){
        	this.conditionList = conditionList;
        }
        
        public Object getChild(int groupPosition, int childPosition) {
            return this.conditionList.get(groupPosition).getSubList().get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return this.conditionList.get(groupPosition).getSubList().size();
        }

        public  TextView getGenericView() {
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
        
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
        	HealthConditionModel condition = (HealthConditionModel) getChild(groupPosition, childPosition);
        	CheckBox textView = new CheckBox(getActivity());
            textView.setPadding(172, 0, 0, 0);
            textView.setText(condition.toString());
            textView.setFocusable(false);
            textView.setFocusableInTouchMode(false);
            textView.setClickable(false);
//            textView.setOnCheckedChangeListener(this);
            textView.setTag(condition.getId());
            textView.setChecked(Utils.listContains(condition.getId(), selConditionList));
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return this.conditionList.get(groupPosition);
        }

        public int getGroupCount() {
            return this.conditionList.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
        
        public void onGroupCollapsed (int groupPosition) {} 
        public void onGroupExpanded(int groupPosition) {}

//		@Override
//		public void onCheckedChanged(CompoundButton buttonView,
//				boolean isChecked) {
//			Integer condition = (Integer) buttonView.getTag();
//            if(!isChecked){
//            	removeCon(condition);
//            }else{
//            	addCon(condition);
//            }
//			
//		}

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
				Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG)
						.show();
			}
		});
	}
}
