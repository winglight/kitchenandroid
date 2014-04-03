package com.syt.health.kitchen.json;

import java.util.Arrays;

/**
 * 点菜助手的一餐菜谱
 * 
 * @author tom
 *
 */
public class OrderMeal {
	private String id;				// 菜谱ID
	private String restaurant;		// 饭店名
	private String date;			// 就餐日期
	private int people;				// 就餐人数
	private String[] desc;			// 对该餐的描述
	private String collocation;		// 营养搭配评价
	private EvalCourse[] evalcourse;// 带评价的菜品
	
	public OrderMeal() {
		super();
	}

	public OrderMeal(String id, String restaurant, String date, int people,
			String[] desc, String collocation, EvalCourse[] evalcourse) {
		super();
		this.id = id;
		this.restaurant = restaurant;
		this.date = date;
		this.people = people;
		this.desc = desc;
		this.collocation = collocation;
		this.evalcourse = evalcourse;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(String restaurant) {
		this.restaurant = restaurant;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	public String[] getDesc() {
		return desc;
	}

	public void setDesc(String[] desc) {
		this.desc = desc;
	}

	public String getCollocation() {
		return collocation;
	}

	public void setCollocation(String collocation) {
		this.collocation = collocation;
	}

	public EvalCourse[] getEvalcourse() {
		return evalcourse;
	}

	public void setEvalcourse(EvalCourse[] evalcourse) {
		this.evalcourse = evalcourse;
	}

	@Override
	public String toString() {
		return "OrderMeal [id=" + id + ", restaurant=" + restaurant + ", date="
				+ date + ", people=" + people + ", desc="
				+ Arrays.toString(desc) + ", collocation=" + collocation
				+ ", evalcourse=" + Arrays.toString(evalcourse) + "]";
	}
}
