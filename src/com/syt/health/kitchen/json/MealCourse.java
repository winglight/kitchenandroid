package com.syt.health.kitchen.json;

import java.io.Serializable;

/**
 * 餐次菜谱中的菜品实体。
 * 包含了菜品，菜品的数量，单位等
 * 
 * @author tom
 *
 */
public class MealCourse implements Serializable {
	private Course course;	// 菜品
	private double quantity;	// 数量
	private String unit;	// 单位
	private String type;	// 特殊标签
	
	public MealCourse() {
		super();
	}

	public MealCourse(Course course, int quantity, String unit) {
		super();
		this.course = course;
		this.quantity = quantity;
		this.unit = unit;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}


	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof MealCourse){
			return this.course.getId().equals(((MealCourse)o).getCourse().getId());
		}
		return false;
	}

	@Override
	public String toString() {
		return "MealCourse [course=" + course + ", quantity=" + quantity
				+ ", unit=" + unit + "]";
	}
	
}
