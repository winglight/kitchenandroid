package com.syt.health.kitchen.widget;

import java.util.List;

import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.MealCourse;

public class FruitWheelAdapter implements WheelAdapter{
	private List<Course> courses;
	public FruitWheelAdapter(List<Course> courses){
		this.courses = courses;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return courses.size();
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		return courses.get(index).getName();
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return 0;
	}
	public String getId(int position){
		return courses.get(position).getId();
		
	}

}
