package com.syt.health.kitchen.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.utils.Utils;

/**
 * 餐次菜谱实体
 * 
 * @author tom
 * 
 */
@JsonAutoDetect
public class Meal implements Serializable {
	/**
	 * 餐次菜谱ID
	 */
	private String id;

	/**
	 * 是否自选。0，智能；1，自选
	 */
	private int isselect;

	/**
	 * 餐次菜谱类型。0，水果；1，早餐；2，午餐；3，晚餐
	 */
	private int type;

	/**
	 * 餐次菜谱里的组成菜品
	 */
	private List<MealCourse> items;

	/**
	 * 所属是日菜谱ID
	 */
	private String menuid;
	
	private String comments;

	public Meal() {
		super();
	}

	public Meal(String id, int isselect, int type, List<MealCourse> items,
			String menuid) {
		super();
		this.id = id;
		this.isselect = isselect;
		this.type = type;
		this.items = items;
		this.menuid = menuid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIsselect() {
		return isselect;
	}

	public void setIsselect(int isselect) {
		this.isselect = isselect;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<MealCourse> getItems() {
		return items;
	}

	public void setItems(List<MealCourse> items) {
		this.items = items;
	}

	public String getMenuid() {
		return menuid;
	}

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments(GenerateCondition smartParams) {
		if(comments == null || comments.length() == 0){
			if(smartParams != null){
				//2. comment cals of the whole day
				int person = smartParams.getPeople();
				int standard_cals = person * Utils.STANDARD_CALS*(type==Utils.BREAKFAST?26:32)/100;
				int mainCals = person * Utils.STANDARD_CALS*18/100;
				if(type==Utils.BREAKFAST || isselect == 1){
					mainCals = 0;
				}
				int total_cals = Utils.calculatetMealCals(getItems(), mainCals);
				comments = Utils.getDivide(total_cals, standard_cals) ;
				//3. judge health goals
				List<Course> courses = Utils.convertMealCourse(getItems());
				 int num_bads = 0;
				 for(Course course : courses){
					 List<String> list = course.getBadDesc(smartParams.getHealthcondition());
					 if(list != null && list.size() > 0){
						 num_bads ++;
					 }
				 }
				 if(num_bads == 0){
					 comments += "所有食物符合设定目标";
				 }else{
					 comments += "<span style=\"background-color:#f88855\"><font color=\"#f88855\">" + num_bads + "个食物不符合设定目标,请区分享用!</font></span>";
				 }
			}
		}
		return comments;
	}
	
	@JsonIgnore
	public String getTotalCals(List<MealCourse> mealCourses, boolean hasZhushi){
		int total_cals = Utils.calculatetMealCals(mealCourses);
		if(type == 1){
			return "已选早点热量:"+total_cals+"千卡";
		}else{
			return "已选" + (hasZhushi?Utils.COURSE_CONDITION_ZHUSHI:"") + "热量:"+total_cals+"千卡";
		}
	}
	
	@JsonIgnore
	public int getSurplusCals(List<MealCourse> mealCourses,int person){
		int total_cals = Utils.calculatetMealCals(mealCourses);
		int totals = person * Utils.STANDARD_CALS;
		int cals;
		if(type==1){
			cals = totals * 26 / 100-total_cals;
		}else{
			cals = totals * 18 / 100-total_cals;
		}		
		return cals;
	}
	
	@JsonIgnore
	public String getAdvicedCals(int person) {
		int cals = 0;
		int rices = 0;

		int totals = person * Utils.STANDARD_CALS;

		if (type == 1) {
			cals = totals * 26 / 100;

			rices = cals * 100 / 350;

			String ret = "早餐推荐热量:" + cals + "千卡\n约" + rices + "克大米或面粉";
			return ret;
		} else {
			cals = totals * 18 / 100;

			rices = cals * 100 / 350;
			
			int courseCals = 0;
			
			if(this.isselect == 0){
			for(MealCourse mc : getItems()){
				if(!Utils.COURSE_CONDITION_ZHUSHI.equals(mc.getCourse().getCoursecond()) || !Utils.COURSE_CONDITION_ZAODIAN.equals(mc.getCourse().getCoursecond())){
					courseCals += mc.getCourse().getCalories();
				}
			}
			if(courseCals < totals*112/1000){
				cals = totals*32/100 - courseCals;
			}
			}

			String ret = "推荐热量:" + cals + "千卡\n约" + rices + "克大米或面粉";
			return ret;
		}
	}
	
	@JsonIgnore
	public String getSetAdvicedCals(int person){
		int cals = 0;

		int totals = person * Utils.STANDARD_CALS;

		if (type == 1) {
			cals = totals * 26 / 100;

			String ret = "早餐推荐热量:" + cals + "千卡";
			return ret;
		} else {
			cals = totals * 18 / 100;

			int courseCals = 0;
			
			if(this.isselect == 0){
			for(MealCourse mc : getItems()){
				if(!Utils.COURSE_CONDITION_ZHUSHI.equals(mc.getCourse().getCoursecond()) || !Utils.COURSE_CONDITION_ZAODIAN.equals(mc.getCourse().getCoursecond())){
					courseCals += mc.getCourse().getCalories();
				}
			}
			if(courseCals < totals*112/1000){
				cals = totals*32/100 - courseCals;
			}
			}

			String ret = "主食推荐热量:" + cals + "千卡";
			return ret;
		}

	}

	@JsonIgnore
	public int getAdvicedPerson() {
		int totals = 0;

		for (MealCourse mc : items) {
			// if (Course.COURSE_CONDITION_ZHUSHI.equals(mc.getCourse()
			// .getCoursecond())) {
			//
			// } else {
			totals += mc.getCourse().getCalories();
			// }
		}
		if (type == 1) {
			return totals / (21 * 26);
		} else {
			return totals / (21 * 32);
		}
	}

	@JsonIgnore
	public List<String> getAllBads() {
		List<String> list = new ArrayList<String>();
		for (MealCourse mc : items) {
			if (mc.getCourse() == null
					|| mc.getCourse().getIncompatible() == null)
				continue;
			for (String bad : mc.getCourse().getIncompatible()) {
				Utils.listAdd(bad, list);
			}
		}
		return list;
	}

	@Override
	public String toString() {
		return "Meal [id=" + id + ", isselect=" + isselect + ", type=" + type
				+ ", items=" + Arrays.toString(items.toArray()) + ", menuid="
				+ menuid + "]";
	}

}
