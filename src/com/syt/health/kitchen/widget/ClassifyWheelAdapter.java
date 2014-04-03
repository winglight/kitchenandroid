package com.syt.health.kitchen.widget;

import java.util.List;

import com.syt.health.kitchen.db.common.HealthCondClassifyModel;

public class ClassifyWheelAdapter implements WheelAdapter{
	private List<HealthCondClassifyModel> classifyModels;
	public ClassifyWheelAdapter(List<HealthCondClassifyModel> classifyModels){
		this.classifyModels = classifyModels;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return classifyModels.size();
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		return classifyModels.get(index).getName();
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	public int getId(int position){
		return classifyModels.get(position).getId();
		
	}

}
