package com.syt.health.kitchen.db;

import java.io.Serializable;
import java.util.List;

import com.syt.health.kitchen.db.common.CacheCourseModel;
import com.syt.health.kitchen.db.common.CacheFoodModel;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.Food;

public class RestaurantModel implements Serializable{

	private String name;
	private String comment;
	private List<Course> courseList;
	private List<Food> foodList;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public List<Course> getCourseList() {
		return courseList;
	}
	public void setCourseList(List<Course> courseList) {
		this.courseList = courseList;
	}
	public List<Food> getFoodList() {
		return foodList;
	}
	public void setFoodList(List<Food> foodList) {
		this.foodList = foodList;
	}
	
	
}
