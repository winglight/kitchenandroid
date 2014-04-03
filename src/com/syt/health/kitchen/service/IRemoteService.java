package com.syt.health.kitchen.service;

import java.util.Date;
import java.util.List;

import com.syt.health.kitchen.db.CourseModel;
import com.syt.health.kitchen.db.MealModel;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseCondition;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.Menu;

public interface IRemoteService {
	
	public void setBaseUrl(String url);
	public String getBaseUrl();

	/**********User Module****************/
	
	/**
	 * Login by device id
	 * @param sid android device id
	 * @return return user's model
	 */
	public UserModel loginDirect(String sid) throws ServiceException;
	
	/**
	 * Login by user's phone number or email
	 * @param phone user's phone number
	 * @param email user's e-mail address
	 * @param password user's password, it should be encrypted in future 
	 * @return if login is successful, return user's model, otherwise, null
	 */
	public UserModel login(String phone,String email, String password) throws ServiceException;
	
	/**
	 * Forget password business by phone or email 
	 * @param phone user's phone number
	 * @param email user's e-mail address
	 * @return if the business is successful, return true, otherwise, false
	 */
	public MessageModel<String> forgetPassword(String phone, String email) throws ServiceException;
	
	/**
	 * User register, take a model with email or phone number
	 * @param user UserModel
	 * @return if the business is successful, return true, otherwise, false
	 */
	public MessageModel<String> register(UserModel user) throws ServiceException;
	
	/**********User Module****************/
	
	/**********Menu Generation************/
	/**
	 * Fetch four planned meals of one day, if not planned, return null;
	 * @param date like, 2012-10-18
	 * @return list of meals
	 */
	public Menu fetchMealsByDate(String date) throws ServiceException;
	
	/**
	 * Smart generate four planned meals of one day, if not planned, return null;
	 * @param date like, 2012-10-18
	 * @param params contains all of conditions of smart generation
	 * @return list of meals
	 * @throws ServiceException
	 */
	public Menu generateMenuByDate(String date, GenerateCondition params) throws ServiceException;
	
	/**
	 * Get menu comments from the remote server
	 * @param id menu id
	 * @return comments
	 * @throws ServiceException
	 */
	public String getMenuCommentsByDate(String date) throws ServiceException;
	
	/**
	 * Get menu nutrients from the remote server
	 * @param date
	 * @return
	 * @throws ServiceException
	 */
	public List<NutrientModel> getMenuNutrientsByDate(String date, List<NutrientModel> list) throws ServiceException;
	
	/**
	 * Create a Menu with four NULL meals
	 * @param date like, 2012-10-18
	 * @return a Menu with four NULL meals
	 * @throws ServiceException
	 */
	public Menu createMenuByDate(String date) throws ServiceException;
	
	/**
	 * Fetch courses by the condition
	 * @param param conditions
	 * @return list of courses
	 * @throws ServiceException
	 */
	public List<Course> fetchCourseByParams(CourseCondition param) throws ServiceException;
	
	
	public List<Course> fetchCourseByCals(int value, boolean ascendOrder, int page) throws ServiceException;
	
	public List<Course> fetchCourseByFood(String name, List<String> health, int page) throws ServiceException;
	
	public List<Food> fetchFoodByHealth(String name, String filter, int page) throws ServiceException;
	
	public List<Food> fetchFoodByNutrient(String name, String filter, int page) throws ServiceException;
	
	public List<Food> fetchFoodByCals(int value, boolean ascendOrder, int page) throws ServiceException;
	
	/**********Menu Generation************/
	
	/**********Meal Management******************/
	
	/**
	 * Update or create a Meal of a date
	 * @param meal Meal instance
	 * @throws ServiceException
	 */
//	public Meal updateOrNewMeal(Meal meal) throws ServiceException;
	
	/**
	 * Add a course to the meal with quantity
	 * @param mealId
	 * @param courseId
	 * @param quantity quantity for course in a meal
	 * @return
	 * @throws ServiceException
	 */
	public void addCourseToMeal(final String mealId, final String courseId, final double quantity) throws ServiceException;
	
	/**
	 * Modify quantity of a course 
	 * @param mealId
	 * @param courseId
	 * @param quantity
	 * @return
	 * @throws ServiceException
	 */
	public void modifyCourseToMeal(final String mealId, final String courseId, final double quantity) throws ServiceException;
	
	/**
	 * Remove a course from the meal
	 * @param mealId
	 * @param courseId
	 */
	public void removeCourseFromMeal(final String mealId, final String courseId) throws ServiceException;
	
	/**
	 * Smart generate one meal of one day, if not planned, return null;
	 * @param meal contains meal id
	 * @param params contains all of conditions of smart generation
	 * @return generated meal
	 * @throws ServiceException
	 */
	public Meal generateMealByHealthCondition(Meal meal, GenerateCondition params) throws ServiceException;
	
	/**********Meal Management******************/
	
	/**********Course Query*************/
	/**
	 * Get course by course id
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public Course queryCourseById(String id) throws ServiceException;
	
	public List<Course> queryCourseByKeyword(String key, boolean filtercond, int page) throws ServiceException;
	
	/**
	 * Get food by food id
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public Food queryFoodById(String id) throws ServiceException;
	
	public List<Food> queryFoodByKeyword(String key, int page) throws ServiceException;
	
	/**********Course Query*************/
	
	public void setSid(String sid) ;
}
