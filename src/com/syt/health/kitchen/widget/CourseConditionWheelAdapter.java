package com.syt.health.kitchen.widget;

import java.util.List;

import com.syt.health.kitchen.db.common.CourseConditionModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.TasteModel;

public class CourseConditionWheelAdapter implements WheelAdapter{
	private List<CourseConditionModel> models;
	public CourseConditionWheelAdapter(List<CourseConditionModel> models){
		this.models = models;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return models.size();
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		return models.get(index).getName();
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	public int getId(int position){
		return models.get(position).getId();
	}

}
