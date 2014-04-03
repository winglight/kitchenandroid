package com.syt.health.kitchen.json;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于查询食材的条件
 * 
 *
 */
public class FoodCondition {
	private int mode = 0; //0 - by health condition search ; 1- by nutrients ; 2 - by cals
	private int cals;
	private boolean ascendOrder = true;
	private String filter;
	private String healthcondition;	// 要符合的养生条件
	private String nutrients;					// 
	private int page;								// 分页。从1开始
	
	public FoodCondition() {
	}
	
	public static FoodCondition newByCalsInstance(){
		FoodCondition cc = new FoodCondition();
		cc.setMode(2);
		return cc;
	}
	
	public static FoodCondition newByNutrientsInstance(){
		FoodCondition cc = new FoodCondition();
		cc.setMode(1);
		cc.setFilter("高");
		return cc;
	}
	
	public static FoodCondition newByHealthInstance(){
		FoodCondition cc = new FoodCondition();
		cc.setMode(0);
		cc.setFilter("宜");
		return cc;
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

	public boolean isAscendOrder() {
		return ascendOrder;
	}

	public void setAscendOrder(boolean ascendOrder) {
		this.ascendOrder = ascendOrder;
	}


	public String getHealthcondition() {
		return healthcondition;
	}

	public void setHealthcondition(String healthcondition) {
		this.healthcondition = healthcondition;
	}

	public String getNutrients() {
		return nutrients;
	}

	public void setNutrients(String nutrients) {
		this.nutrients = nutrients;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

}
