package com.syt.health.kitchen.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 智能生成菜谱的条件
 * 
 * @author tom
 *
 */
public class GenerateCondition implements Serializable {
	/**
	 * 要生成菜谱的所属menuid
	 */
	private String menuid;
	
	private String mealid;
	
	/**
	 * 要生成菜谱的日期
	 */
	private String menudate;
	
	/**
	 * 就餐人数
	 */
	private int people = 1;
	
	/**
	 * 养生需求
	 */
	private List<String> healthcondition = new ArrayList<String>();
	
	public GenerateCondition() {
		super();
	}

	public GenerateCondition(String menuid, String menudate, int people,
			List<String> healthcondition) {
		super();
		this.menuid = menuid;
		this.menudate = menudate;
		this.people = people;
		this.healthcondition = healthcondition;
	}

	public String getMenuid() {
		return menuid;
	}

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public String getMenudate() {
		return menudate;
	}

	public void setMenudate(String menudate) {
		this.menudate = menudate;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}


	public List<String> getHealthcondition() {
		if(healthcondition == null){
			healthcondition = new ArrayList<String>();
		}
		return healthcondition;
	}

	public void setHealthcondition(List<String> healthcondition) {
		this.healthcondition = healthcondition;
	}

	public String getMealid() {
		return mealid;
	}

	public void setMealid(String mealid) {
		this.mealid = mealid;
	}
	
	public GenerateCondition clone(){
		GenerateCondition gc = new GenerateCondition();
		ArrayList<String> strList = new ArrayList<String>();
		strList.addAll(healthcondition);
		gc.setHealthcondition(strList);
		gc.setPeople(people);
		return gc;
	}

	@Override
	public String toString() {
		return "GenerateCondition [menuid=" + menuid + ", menudate=" + menudate
				+ ", people=" + people + ", healthcondition="
				+ healthcondition 
				+ "]";
	}
}
