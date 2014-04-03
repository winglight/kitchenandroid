package com.syt.health.kitchen.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.syt.health.kitchen.db.common.ComnCourseModel;
import com.syt.health.kitchen.service.ServiceImpl;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable{
	public AutoCompleteAdapter(Context context,
			int textViewResourceId) {	
		super(context, textViewResourceId);
		// TODO Auto-generated constructor stub
	}
    private List<ComnCourseModel> values = new ArrayList<ComnCourseModel>();
    private ServiceImpl service;
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		Filter filter = new Filter() {			
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				// TODO Auto-generated method stub
				if(results !=null && results.count >0){
					notifyDataSetChanged();
				}else{
					notifyDataSetInvalidated();
				}		  
			}     		  
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if(constraint != null){  
					values = service.queryCourseByLikeWords(constraint.toString(), 0);
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
		// TODO Auto-generated method stub
		return values.size();
	}
	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return values.get(position).getName();
	}
	public ServiceImpl getService() {
		return service;
	}
	public void setService(ServiceImpl service) {
		this.service = service;
	}
	public String getCourseId(int position){  
		return values.get(position).getId();
	}
}
