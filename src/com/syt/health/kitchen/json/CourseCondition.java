package com.syt.health.kitchen.json;

import java.util.List;

/**
 * 用于查询菜品的条件
 * 
 * @author tom
 *
 */
public class CourseCondition {
	private int mode = 0; //0 - normal search ; 1- by cals ; 2 - by food name
	private int cals;
	private boolean ascendOrder = true;
	private String foodName;
	
	private List<String> healthcondition;	// 要符合的养生条件
	private List<String> taste;					// 口味
	private List<String> coursetype;					// 菜品分类。例如，水果，早点，正餐等
	private List<String> coursecond;				// 菜品种类。荤，素等
//	private String keyword;						// 菜品或食材包含的文字
	private int page;								// 分页。从1开始
	private String filtercond;
	
	private List<String> goodseason;
	
	public CourseCondition() {
	}
	
	public static CourseCondition newByCalsInstance(){
		CourseCondition cc = new CourseCondition();
		cc.setMode(1);
		return cc;
	}
	
	public static CourseCondition newByFoodInstance(){
		CourseCondition cc = new CourseCondition();
		cc.setMode(2);
		return cc;
	}

	public CourseCondition(List<String> healthcondition, List<String> taste,
			List<String> coursecond, int page) {
		super();
		this.healthcondition = healthcondition;
		this.taste = taste;
		this.coursecond = coursecond;
//		this.keyword = condstr;
		this.page = page;
	}

	public List<String> getHealthcondition() {
		return healthcondition;
	}

	public void setHealthcondition(List<String> healthcondition) {
		this.healthcondition = healthcondition;
	}

	public List<String> getTaste() {
		return taste;
	}

	public void setTaste(List<String> taste) {
		this.taste = taste;
	}

	public List<String> getCoursecond() {
		return coursecond;
	}

	public void setCoursecond(List<String> classify) {
		this.coursecond = classify;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<String> getCoursetype() {
		return coursetype;
	}

	public void setCoursetype(List<String> coursetype) {
		this.coursetype = coursetype;
	}

	public String getFiltercond() {
		return filtercond;
	}

	public void setFiltercond(String filtercond) {
		this.filtercond = filtercond;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getCals() {
		return cals;
	}

	public void setCals(int cals) {
		this.cals = cals;
	}

	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}

	public boolean isAscendOrder() {
		return ascendOrder;
	}

	public void setAscendOrder(boolean ascendOrder) {
		this.ascendOrder = ascendOrder;
	}

	public List<String> getGoodseason() {
		return goodseason;
	}

	public void setGoodseason(List<String> goodseason) {
		this.goodseason = goodseason;
	}

	@Override
	public String toString() {
		return "CourseCondition [healthcondition=" + healthcondition
				+ ", taste=" + taste + ", classify=" + coursecond + ", page=" + page + "]";
	}
}
