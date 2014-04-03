package com.syt.health.kitchen.widget;

import java.util.List;

import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.TasteModel;

public class TasteWheelAdapter implements WheelAdapter{
	private List<TasteModel> tasteModels;
	public TasteWheelAdapter(List<TasteModel> tasteModels){
		this.tasteModels = tasteModels;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return tasteModels.size();
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		return tasteModels.get(index).getName();
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	public int getId(int position){
		return tasteModels.get(position).getId();
	}

}
