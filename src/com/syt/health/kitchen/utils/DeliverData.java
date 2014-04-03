package com.syt.health.kitchen.utils;

import java.io.Serializable;
import java.util.List;

import com.syt.health.kitchen.json.CourseFood;

public class DeliverData implements Serializable{
	List<CourseFood>courseFoods;

	public List<CourseFood> getCourseFoods() {
		return courseFoods;
	}

	public void setCourseFoods(List<CourseFood> courseFoods) {
		this.courseFoods = courseFoods;
	}
	
}
