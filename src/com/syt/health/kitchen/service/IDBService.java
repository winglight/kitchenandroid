package com.syt.health.kitchen.service;

import java.util.List;

import com.syt.health.kitchen.db.MealModel;
import com.syt.health.kitchen.db.RestaurantModel;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.CacheCourseModel;
import com.syt.health.kitchen.db.common.CourseConditionModel;
import com.syt.health.kitchen.db.common.CacheFoodModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.db.common.SysModel;
import com.syt.health.kitchen.db.common.TasteModel;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.Menu;

public interface IDBService {

	public void close();
	
	public void setCurrentUser(UserModel user);
	
	//users
	public UserModel queryUserByEmail(String email, String...password);
	public UserModel queryUserByPhone(String phone, String...password);
	public UserModel queryUserBySid(String sid);
	public boolean createUser(UserModel user);
	
	//meals
	/**
	 * Get meals by date
	 * @param date format like, 2012-09-10
	 * @return all of meals of this day
	 */
	public Menu queryMealsByDate(String date);
	
	/**
	 * Save four meals of one day of the user
	 * @param menu 
	 * @param user
	 * @return flag of success
	 */
	public boolean saveMenu(Menu menu, UserModel user, boolean isNewSmart);
	
	/**
	 * delete all of menus after the date
	 * @param date most of case, it's today 
	 * @param user
	 * @return
	 */
	public boolean clearMenu(String date, UserModel user);
	
	public List<HealthCondClassifyModel> getAllHealthCondition();
	
	public List<CourseConditionModel> getAllCourseCondition();
	
	public List<TasteModel> getAllTaste();
	
	public SysModel getDbSysInfo();
	
	public List<NutrientModel> getAllNutrients();
	
	public boolean saveSmartParams(GenerateCondition params, UserModel user);
	
//	public boolean saveOutParams(GenerateCondition params, UserModel user);
//	
	public boolean saveFoodList(List<CourseFood> list, String desc, UserModel user);
//	
//	public boolean saveOutsideHistoryList(List<RestaurantModel> list, UserModel user);
	
	/**
	 * Fetch menus of the user in 7 days
	 * @return
	 */
	public List<Menu> getMenuIn7Days(UserModel user);
	
	/**
	 * Analyze string and search menu by dates
	 * @param courseInfoDesc
	 * @return
	 */
	public List<String> getMenuHealthByDate(String courseInfoDesc);
	
	/**
	 * Fetch menus of 30 days of past
	 * @param user
	 * @return
	 */
	public List<String> getHistoryMenuIn30Days(UserModel user);
	
	/**
	 * Search Course, like %keyword%
	 * @param key keyword
	 * @param courseType Course Type: 0，水果；1，早点；2，正餐
	 * @return Course of JSON package
	 */
//	public List<CacheCourseModel> queryCourseByLikeWords(String key);
	
	/**
	 * Search Food by keyword, like %keyword%
	 * @param key keyword
	 * @return
	 */
//	public List<CacheFoodModel> queryFoodByLikeWords(String key);
	
	public CacheCourseModel getCourseById(String id);
	
	public boolean saveCacheCourse(CacheCourseModel course);
	
	public CacheFoodModel getFoodById(String id);
	
	public boolean saveCacheFood(CacheFoodModel food);
	
}
