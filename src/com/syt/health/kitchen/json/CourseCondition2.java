package com.syt.health.kitchen.json;

import java.util.List;

/**
 * 用于查询菜品的条件
 * 
 * @author tom
 *
 */
public class CourseCondition2 {
	private List<String> healthcondition;	// 要符合的养生条件
	private List<String> taste;					// 口味
	private List<String> coursetype;					// 菜品分类。例如，水果，早点，正餐等
	private List<String> coursecond;				// 菜品种类。荤，素等
//	private String keyword;						// 菜品或食材包含的文字
	private int page;								// 分页。从1开始
	private String filtercond;
	private List<String> goodseason;
	
	public CourseCondition2() {
	}
	
	public CourseCondition2(List<String> healthcondition, List<String> taste,
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

	public List<String> getGoodseason() {
		return goodseason;
	}

	public void setGoodseason(List<String> goodseason) {
		this.goodseason = goodseason;
	}

	public static CourseCondition2 copyCondition(CourseCondition cc){
		CourseCondition2 cc2 = new CourseCondition2();
		cc2.setCoursecond(cc.getCoursecond());
		cc2.setCoursetype(cc.getCoursetype());
		cc2.setFiltercond(cc.getFiltercond());
		cc2.setHealthcondition(cc.getHealthcondition());
		cc2.setPage(cc.getPage());
		cc2.setTaste(cc.getTaste());
		cc2.setGoodseason(cc.getGoodseason());
		
		return cc2;
	}
	@Override
	public String toString() {
		return "CourseCondition [healthcondition=" + healthcondition
				+ ", taste=" + taste + ", classify=" + coursecond + ", page=" + page + "]";
	}
}
