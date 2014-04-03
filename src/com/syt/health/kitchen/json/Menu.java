package com.syt.health.kitchen.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.utils.Utils;

/**
 * 是日菜谱的实体
 * 
 * @author tom
 * 
 */
@JsonAutoDetect
public class Menu implements Serializable {
	/**
	 * 是日菜谱ID
	 */
	private String id;

	/**
	 * 早餐餐次菜谱
	 */
	private Meal fruit;

	/**
	 * 早餐餐次菜谱
	 */
	private Meal breakfast;

	/**
	 * 午餐餐次菜谱
	 */
	private Meal lunch;

	/**
	 * 晚餐餐次菜谱
	 */
	private Meal dinner;

	/**
	 * 是日菜谱的日期
	 */
	private String menudate;

	private String comments;

	private GenerateCondition smartParams;

	private List<NutrientModel> nutrients;

	public Menu() {
		super();
	}

	public Menu(String id, Meal fruit, Meal breakfast, Meal lunch, Meal dinner,
			String menudate) {
		super();
		this.id = id;
		this.fruit = fruit;
		this.breakfast = breakfast;
		this.lunch = lunch;
		this.dinner = dinner;
		this.menudate = menudate;
	}

	public void setMeal(Meal meal) {
		switch (meal.getType()) {
		case 0: {
			setFruit(meal);
			break;
		}
		case 1: {
			setBreakfast(meal);
			break;
		}
		case 2: {
			setLunch(meal);
			break;
		}
		case 3: {
			setDinner(meal);
			break;
		}
		}
	}

	@JsonIgnore
	public Meal getMealByType(int type) {
		switch (type) {
		case 0: {
			return getFruit();
		}
		case 1: {
			return getBreakfast();
		}
		case 2: {
			return getLunch();
		}
		case 3: {
			return getDinner();
		}
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Meal getFruit() {
		return fruit;
	}

	public void setFruit(Meal fruit) {
		this.fruit = fruit;
	}

	public Meal getBreakfast() {
		return breakfast;
	}

	public void setBreakfast(Meal breakfast) {
		this.breakfast = breakfast;
	}

	public Meal getLunch() {
		return lunch;
	}

	public void setLunch(Meal lunch) {
		this.lunch = lunch;
	}

	public Meal getDinner() {
		return dinner;
	}

	public void setDinner(Meal dinner) {
		this.dinner = dinner;
	}

	public String getMenudate() {
		return menudate;
	}

	public void setMenudate(String menudate) {
		this.menudate = menudate;
	}

	public List<NutrientModel> getNutrients() {
		return nutrients;
	}

	public void setNutrients(List<NutrientModel> nutrients) {
		if (nutrients != null && nutrients.size() > 0) {
			NutrientModel nm = nutrients.get(0);
			if (nm.getContent() <= 0) {
				// calculate cals
				nm.setContent(getTotalCals());
			}
			NutrientModel nmb1 = new NutrientModel();
			nmb1.setName(NutrientModel.NUTRIENT_B1);
			NutrientModel nmb2 = new NutrientModel();
			nmb2.setName(NutrientModel.NUTRIENT_B2);
			nutrients.remove(nmb1);
			nutrients.remove(nmb2);
		}
		this.nutrients = nutrients;
	}

	@JsonIgnore
	public int getTotalCals() {
		int person = smartParams.getPeople();
		int totals = person * Utils.STANDARD_CALS;
		int cals = totals * 18 / 100;

		int total_cals = Utils.calculatetMealCals(getFruit().getItems());
		total_cals += Utils.calculatetMealCals(getBreakfast().getItems());
		total_cals += Utils.calculatetMealCals(getLunch().getItems(), ((getLunch().getIsselect() == 0)?cals:0));
		total_cals += Utils.calculatetMealCals(getDinner().getItems(), ((getDinner().getIsselect() == 0)?cals:0));

		return total_cals;
	}

	@JsonIgnore
	public String getComments() {
		if (comments == null || comments.length() == 0) {
			if (smartParams != null && nutrients != null) {
				int person = smartParams.getPeople();

				// 1. judge nutrients balance
				int num_perfect = 0;
				int num_good = 0;
				int num_bad = 0;
				int total_cals = 0;

				// 2. comment cals of the whole day

				int standard_cals = person * Utils.STANDARD_CALS;
				total_cals = (int) nutrients.get(0).getContent();
				comments = Utils.getDivide(total_cals, standard_cals);
				// 3. judge health goals
				List<Course> courses = Utils.convertMealCourse(getLunch()
						.getItems());
				courses.addAll(Utils.convertMealCourse(getDinner().getItems()));
				courses.addAll(Utils.convertMealCourse(getFruit().getItems()));
				courses.addAll(Utils.convertMealCourse(getBreakfast()
						.getItems()));
				int num_bads = 0;
				for (Course course : courses) {
					List<String> list = course.getBadDesc(smartParams.getHealthcondition());
					if (list != null && list.size() > 0) {
						num_bads++;
					}
				}
				if (num_bads == 0) {
					comments += "所有食物符合设定目标<BR>";
				} else {
					comments += "<span style=\"background-color:#f88855\"><font color=\"#f88855\">"
							+ num_bads + "个食物不符合设定目标,请区分享用!</font></span><BR>";
				}

				String nA = "";
				String nB = "";
				String nC = "";
				String health = Utils.arrayIntoString(smartParams.getHealthcondition());
				for (NutrientModel nm : nutrients) {
					if (nm.getName().equals(Utils.RE_LIANG)) {
						total_cals = (int) nm.getContent();
					} else {
						boolean isPoor = false;
						String desc = nm.getCurrentPositionDesc(person);
						if (desc.equals(NutrientModel.FIELD_START)) {
							num_bad++;
							isPoor = true;
						} else if (desc.equals(NutrientModel.FIELD_PERFECT)) {
							num_perfect++;
						} else {
							num_good++;
						}
						
						if (nm.getName().equals(Utils.NUTRIENT_PROTEIN)) {
							if(health.contains(Utils.HEALTH_YOUTH)){
								if(!nB.contains(Utils.NUTRIENT_PROTEIN)){
									nB += Utils.NUTRIENT_PROTEIN + ",";
								}
								if(isPoor){
									nA += "蛋白质不足不利于青少年健康发育!<BR>";
								}
								nC += "适量额外补充钙和复合维生素,有利于青少年发育.<BR>";
							}
							if(health.contains(Utils.HEALTH_OPT)){
								if(!nB.contains(Utils.NUTRIENT_PROTEIN)){
									nB += Utils.NUTRIENT_PROTEIN + ",";
								}
								if(isPoor){
									nA += "蛋白质不足不利于术后康复.<BR>";
								}
							}
						}else if (nm.getName().equals(Utils.NUTRIENT_IRON)) {
							if(health.contains(Utils.HEALTH_POOR_BLOOD)){
								if(!nB.contains(Utils.NUTRIENT_IRON)){
									nB += Utils.NUTRIENT_IRON + ",";
								}
								if(isPoor){
									nA += "铁不足不利于贫血!<BR>";
								}
							}
							if(health.contains(Utils.HEALTH_SUP_BLOOD)){
								if(!nB.contains(Utils.NUTRIENT_IRON)){
									nB += Utils.NUTRIENT_IRON + ",";
								}
								if(isPoor){
									nA += "铁不足不利于补血!<BR>";
								}
							}
						}else if (nm.getName().equals(Utils.NUTRIENT_VC)) {
							if(health.contains(Utils.HEALTH_BEAUTY)){
								if(!nB.contains(Utils.NUTRIENT_VC)){
									nB += Utils.NUTRIENT_VC + ",";
								}
								if(isPoor){
									nA += "维C不足不利于美容!<BR>";
								}
							}
						}else if (nm.getName().equals(Utils.NUTRIENT_FOLIC)) {
							if(health.contains(Utils.HEALTH_PREG)){
								if(!nB.contains(Utils.NUTRIENT_FOLIC)){
									nB += Utils.NUTRIENT_FOLIC + ",";
								}
								if(isPoor){
									nA += "饮食很难保证摄入足够叶酸,请孕妇遵医嘱额外补充.<BR>";
								}
							}
							if(health.contains(Utils.HEALTH_PRE_PREG)){
								if(!nB.contains(Utils.NUTRIENT_FOLIC)){
									nB += Utils.NUTRIENT_FOLIC + ",";
								}
								if(isPoor){
									nA += "饮食很难保证摄入足够叶酸,备孕期可遵医嘱额外补充.<BR>";
								}
							}
						}
					}
					
				}
				if(nB.endsWith(",")){
					nB = nB.substring(0, nB.length()-1);
				}
				if(nB.length() > 0){
					nB = "依据设定的养生目标,需要保证" + nB + "的摄入量.<BR>";
				}
				if(health.contains(Utils.HEALTH_KIDEY)){
					nC += "蛋白质过多不利于肾炎患者,请区分享用每餐中适宜肾炎的食物.<BR>";
				}
				if(health.contains(Utils.HEALTH_DIABETES)){
					nC += "请糖尿病患者区分享用每餐中适宜糖尿病的食物.<BR>";
				}
				if(health.contains(Utils.HEALTH_OLD)){
					nC += "老年人可适量额外补充复合维生素和钙.<BR>";
				}
				if(health.contains(Utils.HEALTH_MENO_PAUSE)){
					nC += "更年期女性可适量额外补充大豆异黄酮和钙.<BR>";
				}
				if (num_bad > 0) {
					if(nA.length() > 0){
						comments += "<span style=\"background-color:#f88855\"><font color=\"#f88855\">"
								+ nA + "</font></span>";
					}else{
						comments += "基础营养素搭配合理<BR>";
					}
				} else if (num_good > 0) {
					comments += "所有营养素搭配良好<BR>";
				} else {
					comments += "完美符合营养搭配标准<BR>";
				}
				comments += nB + nC;
			}
		}
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public GenerateCondition getSmartParams() {
		return smartParams;
	}

	public void setSmartParams(GenerateCondition smartParams) {
		this.smartParams = smartParams;
	}

	@Override
	public String toString() {
		return "Menu [id=" + id + ", fruit=" + fruit + ", breakfast="
				+ breakfast + ", lunch=" + lunch + ", dinner=" + dinner
				+ ", menudate=" + menudate + "]";
	}

	@JsonIgnore
	public int getFruitAdviceCal(int numPeople) {
		return numPeople * Utils.STANDARD_CALS / 10;
		// return "推荐水果热量:"+numPeople*Utils.STANDARD_CALS/10+"千卡";
	}

	@JsonIgnore
	public double getTotalFruitCal() {
		double total = 0;
		List<MealCourse> mealCourses = fruit.getItems();
		for (MealCourse mealCourse : mealCourses) {
			double quantity = mealCourse.getQuantity();
			int calories = mealCourse.getCourse().getCalories();
			total = total + quantity * calories;
		}
		return total;
		// return "已选水果热量:"+new Double(total).intValue()+"千卡";
	}
}
