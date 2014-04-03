package com.syt.health.kitchen.json;

import java.io.Serializable;

/**
 * 菜品中的食材实体
 * 
 * @author tom
 *
 */
public class CourseFood implements Serializable{
	public static final String FOOD_FIELD = "food";
	public static final String QUANTITY_FIELD = "quantity";
	public static final String FOODUNIT_FIELD = "foodunit";
	public static final String FOODTYPE_FIELD = "foodtype";
	public static final String FOODTYPE_ZHULIAO = "主料";
	public static final String FOODTYPE_FULIAO = "辅料";
	public static final String FOODTYPE_PEILIAO = "调料";
	
	private Food food;			// 食材
	private int quantity;		// 数量
	private String foodunit;	// 单位
	private String foodtype;	// 食材类型。主料，辅料，配料
	private boolean flag;		//是否已购买
	
	public CourseFood() {
		super();
	}

	public CourseFood(Food food, int quantity, String foodunit, String foodtype) {
		super();
		this.food = food;
		this.quantity = quantity;
		this.foodunit = foodunit;
		this.foodtype = foodtype;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public Food getFood() {
		return food;
	}

	public void setFood(Food food) {
		this.food = food;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getFoodunit() {
		return foodunit;
	}

	public void setFoodunit(String foodunit) {
		this.foodunit = foodunit;
	}

	public String getFoodtype() {
		return foodtype;
	}

	public void setFoodtype(String foodtype) {
		this.foodtype = foodtype;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((food == null) ? 0 : food.hashCode());
		result = prime * result
				+ ((foodtype == null) ? 0 : foodtype.hashCode());
		result = prime * result
				+ ((foodunit == null) ? 0 : foodunit.hashCode());
		result = prime * result + quantity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseFood other = (CourseFood) obj;
		if (food == null) {
			if (other.food != null)
				return false;
		} else if (!food.getId().equals(other.food.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CourseFood [food=" + food + ", quantity=" + quantity
				+ ", foodunit=" + foodunit + ", foodtype=" + foodtype + "]";
	}
}
