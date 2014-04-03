package com.syt.health.kitchen.widget;

import java.util.List;

import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.NutrientModel;

public class NutrientsWheelAdapter implements WheelAdapter{
	private List<NutrientModel> nutrientModels;
	public NutrientsWheelAdapter(List<NutrientModel> nutrientModels){
		this.nutrientModels = nutrientModels;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return nutrientModels.size();
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		return nutrientModels.get(index).getName();
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	public int getId(int position){
		return nutrientModels.get(position).getId();
		
	}

}
