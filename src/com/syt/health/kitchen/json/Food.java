package com.syt.health.kitchen.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.utils.Utils;

public class Food implements Serializable,GoodBadConflictComparable{
	public final static String ID_FIELD = "id";
	public final static String NAME_FIELD = "name";
	public final static String EFFECTIVITY_FIELD = "effectivity";
	public final static String INCOMPATIBLE_FIELD = "incompatible";
	public final static String ANTIFOOD_FIELD = "antifood";
	public final static String BIBLEURL_FIELD = "bibleurl";
	
	private String id;				// 食材ID
	private String name;			// 食材名称
	private List<String> effectivity;	// 适宜的养生情况
	private List<String> incompatible;	// 不宜的养生情况
	private List<String> antifood;		// 相克的食材
	private String bibleurl;		// 采购宝典URL
	
	private String pinyin;					// 食材名称拼音全拼
	private String picurl;					// 食材图片
	private String bibletext;				// 采购宝典
	private List<String> goodseason;		// 适宜季节
	private double calories;				// 标准单位热量
	private String unit;					// 标准单位
	private int classify;					// 食材分类。0-主料,配料，1-调味料
	private String type;					//类型ID(数据字典中确定)谷类...xxx类...
	private List<NutrientModel> nutrients;	//该食材所包含的营养素
	private NutrientModel nutrient;	//该食材所包含的营养素
	
	private int hasbible; //是否有采购宝典(1是0否)
	
	public Food() {
		super();
	}

	public Food(String id, String name, List<String> effectivity,
			List<String> incompatible, List<String> antifood, String bibleurl) {
		super();
		this.id = id;
		this.name = name;
		this.effectivity = effectivity;
		this.incompatible = incompatible;
		this.antifood = antifood;
		this.bibleurl = bibleurl;
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

	public List<String> getAntifood() {
		return antifood;
	}

	public void setAntifood(List<String> antifood) {
		this.antifood = antifood;
	}

	public String getBibleurl() {
		return bibleurl;
	}

	public void setBibleurl(String bibleurl) {
		this.bibleurl = bibleurl;
	}

	@Override
	public boolean equals(Object obj) {
		Food food = (Food)obj;
		
		return food.getId().equalsIgnoreCase(id);
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getBibletext() {
		return bibletext;
	}

	public void setBibletext(String bibletext) {
		this.bibletext = bibletext;
	}

	public List<String> getGoodseason() {
		return goodseason;
	}

	public void setGoodseason(List<String> goodseason) {
		this.goodseason = goodseason;
	}

	public double getCalories() {
		return calories;
	}

	public void setCalories(double calories) {
		this.calories = calories;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getClassify() {
		return classify;
	}

	public void setClassify(int classify) {
		this.classify = classify;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<NutrientModel> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<NutrientModel> nutrients) {
		this.nutrients = nutrients;
	}

	public NutrientModel getNutrient() {
		return nutrient;
	}

	public void setNutrient(NutrientModel nutrient) {
		this.nutrient = nutrient;
	}

	public int getHasbible() {
		return hasbible;
	}

	public void setHasbible(int hasbible) {
		this.hasbible = hasbible;
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
	
	@JsonIgnore
	public boolean hasBible(){
		return this.hasbible == 1;
	}

	@Override
	public String toString() {
		return "Food [id=" + id + ", name=" + name + ", effectivity="
				+ effectivity + ", incompatible=" + incompatible
				+ ", antifood=" + antifood + ", bibleurl=" + bibleurl + "]";
	}

	@Override
	@JsonIgnore
	public boolean isKeWith(Course course) {
		if(antifood != null && antifood.size() > 0){
			List<Food> cFoodList = course.getMainFoodList();
			for(Food f:cFoodList){
				if(f.isKeWith(this)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@JsonIgnore
	public boolean isKeWith(Food food) {
		if(antifood != null && antifood.size() > 0){
			List<String> cFoodList = food.getAntifood();
			if(cFoodList == null) return false;
			
			for(String f:cFoodList){
				if(f.equals(this.getName())){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@JsonIgnore
	public List<String> getGoodDesc(List<String> health) {
		if(health == null || effectivity == null || effectivity.size() == 0) return new ArrayList<String>();
		
		List<String> goods = new ArrayList<String>();
		if(health != null){
			for(String h : health){
				if(Utils.listContains(h, effectivity)){
					goods.add(h);
				}
			}
		}
		
		return goods;
	}

	@Override
	@JsonIgnore
	public List<String> getBadDesc(List<String> health) {
		if(health == null || incompatible == null || incompatible.size() == 0) return new ArrayList<String>();
		
		List<String> bads = new ArrayList<String>();
		if(health != null){
			for(String h : health){
				if(Utils.listContains(h, incompatible)){
					bads.add(h);
				}
			}
		}
		
		return bads;
	}

	@Override
	@JsonIgnore
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
