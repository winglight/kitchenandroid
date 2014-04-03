package com.syt.health.kitchen.widget;

import java.util.List;

import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.HealthConditionModel;

public class ConditionWheelAdapter implements WheelAdapter{
	private List<HealthConditionModel> conditionModels;
	public ConditionWheelAdapter(List<HealthConditionModel> conditionModels){
		this.conditionModels = conditionModels;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return conditionModels.size();
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		return conditionModels.get(index).getName();
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}
