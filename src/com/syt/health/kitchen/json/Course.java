package com.syt.health.kitchen.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syt.health.kitchen.utils.Utils;

/**
 * 菜品实体
 * 
 * @author tom
 * 
 */
public class Course implements Serializable, GoodBadConflictComparable {

	private String id; // 菜品ID
	private String name; // 菜品名称
	private String picurl; // 菜品图片
	private String coursetype; // 菜品餐次分类。早点，水果，正餐
	private String coursecond; // 菜品饮食分类。主食，禽肉，畜肉，水产，素菜，汤。
	private String taste; // 菜品口味.
	private String unit; // 菜品标准单位
	private double calories; // 菜品标准单位热量
	private List<String> effectivity; // 适宜的养生情况
	private List<String> incompatible; // 不宜的养生情况
	
	
	private List<CourseFood> items; // 组成的食材
	private List<String> cookmethod; // 烹饪方法
	private String precooktime; // 烹饪准备时间
	private String totalcooktime; // 烹饪总耗时
	private List<CookPractice> cookpractice;// 烹饪步骤
	private String comment;

	public Course() {
		super();
	}

	public Course(String id, String name, String picurl, String coursetype,
			String coursecond, String taste, String unit, int calories,
			List<String> effectivity, List<String> incompatible,
			List<CourseFood> items, List<String> cookmethod,
			String precooktime, String totalcooktime,
			List<CookPractice> cookpractice) {
		super();
		this.id = id;
		this.name = name;
		this.picurl = picurl;
		this.coursetype = coursetype;
		this.coursecond = coursecond;
		this.taste = taste;
		this.unit = unit;
		this.calories = calories;
		this.effectivity = effectivity;
		this.incompatible = incompatible;
		this.items = items;
		this.cookmethod = cookmethod;
		this.precooktime = precooktime;
		this.totalcooktime = totalcooktime;
		this.cookpractice = cookpractice;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getCoursetype() {
		return coursetype;
	}

	public void setCoursetype(String coursetype) {
		this.coursetype = coursetype;
	}

	public String getCoursecond() {
		return coursecond;
	}

	public void setCoursecond(String coursecond) {
		this.coursecond = coursecond;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getCalories() {
		return (int)calories;
	}

	public void setCalories(int calories) {
		this.calories = calories;
	}

	public List<String> getEffectivity() {
		return effectivity;
	}

	public void setEffectivity(List<String> effectivity) {
		this.effectivity = effectivity;
	}

	public List<String> getIncompatible() {
		return incompatible;
	}

	public void setIncompatible(List<String> incompatible) {
		this.incompatible = incompatible;
	}

	public List<CourseFood> getItems() {
		return items;
	}

	public void setItems(List<CourseFood> items) {
		this.items = items;
	}

	public List<String> getCookmethod() {
		return cookmethod;
	}

	public void setCookmethod(List<String> cookmethod) {
		this.cookmethod = cookmethod;
	}

	public String getPrecooktime() {
		return precooktime;
	}

	public void setPrecooktime(String precooktime) {
		this.precooktime = precooktime;
	}

	public String getTotalcooktime() {
		return totalcooktime;
	}

	public void setTotalcooktime(String totalcooktime) {
		this.totalcooktime = totalcooktime;
	}

	public List<CookPractice> getCookpractice() {
		return cookpractice;
	}

	public void setCookpractice(List<CookPractice> cookpractice) {
		this.cookpractice = cookpractice;
	}

	public String getTaste() {
		return taste;
	}

	public void setTaste(String taste) {
		this.taste = taste;
	}
	@JsonIgnore
	public String getListPicUrl(){
		if(picurl != null){
			int pos = picurl.lastIndexOf(".");
			return this.picurl.substring(0, pos) + "_list" + this.picurl.substring(pos);
			}else{
				return "";
			}
	}

	@Override
	public boolean equals(Object obj) {
		Course course = (Course) obj;

		return course.getId().equalsIgnoreCase(id);
	}

	@Override
	public String toString() {
		return "Course [id=" + id + ", name=" + name + ", picurl=" + picurl
				+ ", coursetype=" + coursetype + ", coursecond=" + coursecond
				+ ", taste=" + taste + ", unit=" + unit + ", calories="
				+ calories + ", effectivity=" + effectivity + ", incompatible="
				+ incompatible + ", items=" + items + ", cookmethod="
				+ cookmethod + ", precooktime=" + precooktime
				+ ", totalcooktime=" + totalcooktime + ", cookpractice="
				+ cookpractice + "]";
	}

	public List<Food> getMainFoodList() {
		List<Food> list = new ArrayList<Food>();
		for (CourseFood cf : items) {
			if ("主料".equals(cf.getFoodtype())) {
				list.add(cf.getFood());
			}
		}
		return list;
	}

	@Override
	public boolean isKeWith(Course course) {
		List<Food> cFoodList = getMainFoodList();
		for (Food f : cFoodList) {
			if (f.isKeWith(course)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isKeWith(Food food) {
		List<Food> cFoodList = getMainFoodList();
		for (Food f : cFoodList) {
			if (f.isKeWith(food)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getGoodDesc(List<String> health) {
		if (health == null || effectivity == null || effectivity.size() == 0)
			return new ArrayList<String>();

		List<String> goods = new ArrayList<String>();
		if (health != null) {
			for (String h : health) {
				if (Utils.listContains(h, effectivity)) {
					goods.add(h);
				}
			}
		}

		return goods;
	}

	@Override
	public List<String> getBadDesc(List<String> health) {
		if (health == null || incompatible == null || incompatible.size() == 0)
			return new ArrayList<String>();

		List<String> bads = new ArrayList<String>();
		if (health != null) {
			for (String h : health) {
				if (Utils.listContains(h, incompatible)) {
					bads.add(h);
				}
			}
		}

		return bads;
	}

	@Override
	public List<String> getConflictDesc(List<GoodBadConflictComparable> list) {
		if (list == null || list.size() == 0)
			return new ArrayList<String>();
		
		List<String> conflicts = new ArrayList<String>();
		for(GoodBadConflictComparable gbcc : list){
			if(gbcc.isKeWith(this)){
				conflicts.add(gbcc.getName());
			}
		}
		
		return conflicts;
	}

}
