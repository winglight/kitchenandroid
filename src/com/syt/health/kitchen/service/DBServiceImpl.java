package com.syt.health.kitchen.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.syt.health.kitchen.db.CommonDBOpenHelper;
import com.syt.health.kitchen.db.MealModel;
import com.syt.health.kitchen.db.MenuModel;
import com.syt.health.kitchen.db.RestaurantModel;
import com.syt.health.kitchen.db.UserDBOpenHelper;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.CacheCourseModel;
import com.syt.health.kitchen.db.common.CourseConditionModel;
import com.syt.health.kitchen.db.common.CacheFoodModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.HealthConditionModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.db.common.SysModel;
import com.syt.health.kitchen.db.common.TasteModel;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.utils.DateUtils;

public class DBServiceImpl implements IDBService {

	private static final String LOG_TAG = "DBServiceImpl";

	private UserDBOpenHelper userHelper;
	private CommonDBOpenHelper commonHelper;
	private ObjectMapper mapper = new ObjectMapper();
	private UserModel currentUser;

	private DBServiceImpl(Context context) {
		this.commonHelper = CommonDBOpenHelper.getHelper(context);
		this.userHelper = UserDBOpenHelper.getHelper(context);
	}

	public static IDBService getInstance(Context context) {
		return new DBServiceImpl(context);
	}
 
	@Override
	public void close() {
		if (userHelper != null || commonHelper != null) {
			OpenHelperManager.releaseHelper();
			userHelper = null;
			commonHelper = null;
		}
	}

	public UserDBOpenHelper getUserHelper() {
		return userHelper;
	}

	public CommonDBOpenHelper getCommonHelper() {
		return commonHelper;
	}

	@Override
	public UserModel queryUserByEmail(String email, String... password) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			QueryBuilder<UserModel, Integer> queryBuilder = udao.queryBuilder();
			Where<UserModel, Integer> where = queryBuilder.where();
			where.eq(UserModel.FIELD_EMAIL, email);
			if (password != null) {
				where.and();
				where.eq(UserModel.FIELD_PASSWORD, password[0]);
			}

			return udao.queryForFirst(queryBuilder.prepare());

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public UserModel queryUserByPhone(String phone, String... password) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			QueryBuilder<UserModel, Integer> queryBuilder = udao.queryBuilder();
			Where<UserModel, Integer> where = queryBuilder.where();
			where.eq(UserModel.FIELD_PHONE_NUMBER, phone);
			if (password != null) {
				where.and();
				where.eq(UserModel.FIELD_PASSWORD, password[0]);
			}

			return udao.queryForFirst(queryBuilder.prepare());

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public boolean createUser(UserModel user) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			// 1.query user by phone and email
			if (checkDuplicatedUser(user)) {

			} else {
				udao.create(user);
			}

			return true;
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}

	@Override
	public Menu queryMealsByDate(String date) {
		try {
			Dao<MenuModel, Integer> udao = userHelper.getMenuDAO();

			QueryBuilder<MenuModel, Integer> queryBuilder = udao.queryBuilder();
			Where<MenuModel, Integer> where = queryBuilder.where();
			where.eq(MenuModel.FIELD_DATE, date);
			if (currentUser != null) {
				where.and();
				where.eq(MenuModel.FIELD_USER, currentUser);
			}

			MenuModel menuModel = udao.queryForFirst(queryBuilder.prepare());

			if (menuModel == null)
				return null;
			String json = menuModel.getJson();
			if (json == null)
				return null;
			
			Menu menu = null;
			try {
				menu = mapper.readValue(json, Menu.class);
			} catch (JsonParseException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (JsonMappingException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			GenerateCondition sp = menuModel.getObjSmartParams();
			menu.setSmartParams(sp);
			
			return menu;

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public boolean saveMenu(Menu menu, UserModel user, boolean isNewSmart) {
		try {
			Dao<MenuModel, Integer> udao = userHelper.getMenuDAO();

			QueryBuilder<MenuModel, Integer> queryBuilder = udao.queryBuilder();
			Where<MenuModel, Integer> where = queryBuilder.where();
			where.eq(MenuModel.FIELD_DATE, menu.getMenudate());
			where.and();
			where.eq(MenuModel.FIELD_USER, user);

			MenuModel menuModel = udao.queryForFirst(queryBuilder.prepare());
			if (menuModel == null) {
				menuModel = new MenuModel();
				menuModel.setOid(menu.getId());
				menuModel.setDate(menu.getMenudate());
				menuModel.setUser(user);
				try {
					menuModel.setJson( mapper.writeValueAsString(menu));
				} catch (JsonProcessingException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
				
					menuModel.setObjSmartParams(user.getObjSmartParams());

				udao.create(menuModel);
			} else {
				menuModel.setOid(menu.getId());
				menuModel.setDate(menu.getMenudate());
				menuModel.setUser(user);
				try {
					menuModel.setJson( mapper.writeValueAsString(menu));
				} catch (JsonProcessingException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
				
				if(isNewSmart){
					menuModel.setObjSmartParams(user.getObjSmartParams());
				}
				
				udao.update(menuModel);
			}

			return true;
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}catch (RuntimeException re) {

			Log.e(LOG_TAG, re.getMessage());
		}
		return false;
	}

	@Override
	public void setCurrentUser(UserModel user) {
		this.currentUser = user;

	}

	public boolean checkDuplicatedUser(UserModel user) {
		if (user == null)
			return false;
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			if (user.getPhoneNumber() != null
					&& user.getPhoneNumber().length() > 0) {
				List<UserModel> list;

				list = udao.queryForEq(UserModel.FIELD_PHONE_NUMBER,
						user.getPhoneNumber());

				if (list != null && list.size() > 0) {
					return true;
				}
			}

			if (user.getEmail() != null && user.getEmail().length() > 0) {
				List<UserModel> list;

				list = udao.queryForEq(UserModel.FIELD_EMAIL, user.getEmail());

				if (list != null && list.size() > 0) {
					return true;
				}
			}
		} catch (SQLException e) {
			Log.e(LOG_TAG, "DB error:" + e.getMessage());
		}
		return false;
	}

	@Override
	public List<HealthCondClassifyModel> getAllHealthCondition() {
		try {
			Dao<HealthCondClassifyModel, Integer> hdao = commonHelper
					.getHealthCondClassifyDao();
			Dao<HealthConditionModel, Integer> h2dao = commonHelper
					.getHealthConditionDao();

			List<HealthCondClassifyModel> list = hdao.queryForAll();

			for (HealthCondClassifyModel hccm : list) {
				List<HealthConditionModel> hcm = h2dao.queryForEq(
						HealthConditionModel.FIELD_CLASSIFY, hccm.getId());
				hccm.setSubList(hcm);
			}

			return list;

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public List<CourseConditionModel> getAllCourseCondition() {
		try {
			Dao<CourseConditionModel, Integer> hdao = commonHelper
					.getCourseConditionDao();

			return hdao.queryForAll();

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public List<TasteModel> getAllTaste() {
		try {
			Dao<TasteModel, Integer> hdao = commonHelper.getTasteDao();

			return hdao.queryForAll();

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<NutrientModel> getAllNutrients() {
		try {
			Dao<NutrientModel, Integer> hdao = commonHelper.getNutrientDao();

			return hdao.queryForAll();

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public SysModel getDbSysInfo() {
		try {
			Dao<SysModel, Integer> hdao = commonHelper.getSysDao();

			return hdao.queryForAll().get(0);

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public CacheCourseModel getCourseById(String id) {
		try {
			Dao<CacheCourseModel, String> hdao = userHelper.getCourseDao();

			return hdao.queryForId(id);

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}
	
	@Override
	public CacheFoodModel getFoodById(String id) {
		try {
			Dao<CacheFoodModel, String> hdao = userHelper.getFoodDao();

			return hdao.queryForId(id);

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}
	
	@Override
	public boolean saveCacheCourse(CacheCourseModel course) {
		try {
			Dao<CacheCourseModel, String> hdao = userHelper.getCourseDao();

			hdao.createOrUpdate(course);
			
			return true;

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}

	@Override
	public boolean saveCacheFood(CacheFoodModel food) {
		try {
			Dao<CacheFoodModel, String> hdao = userHelper.getFoodDao();

			 hdao.createOrUpdate(food);
			return true;

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}

	@Override
	public UserModel queryUserBySid(String sid) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			List<UserModel> list = udao.queryForEq(UserModel.FIELD_SID, sid);

			if (list != null && list.size() > 0) {
				return list.get(0);
			}

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public boolean saveSmartParams(GenerateCondition params, UserModel user) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			user.setObjSmartParams(params);

				udao.update(user);
				
			return true;
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}

	
	
	@Override
	public boolean saveFoodList(List<CourseFood> list, String desc, UserModel user) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			user.setObjFoodList(list);
			if(desc != null){
			user.setFoodListDesc(desc);
			}

				udao.update(user);

			return true;
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}

	/**
	@Override
	public boolean saveOutParams(GenerateCondition params, UserModel user) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			user.setObjOutParams(params);

				udao.update(user);

			return true;
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean saveOutsideHistoryList(List<RestaurantModel> list,
			UserModel user) {
		try {
			Dao<UserModel, Integer> udao = userHelper.getUserDAO();

			user.setObjOutsideHistoryList(list);

				udao.update(user);

			return true;
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}
	*/

	@Override
	public List<Menu> getMenuIn7Days(UserModel user) {
		try {
			Date today = new Date();
			String begin = DateUtils.defaultFormat(today);
			String end = DateUtils.defaultFormat(DateUtils.addDateDays(today, 6));
			
			Dao<MenuModel, Integer> hdao = userHelper.getMenuDAO();

			QueryBuilder<MenuModel, Integer> queryBuilder = hdao
					.queryBuilder();
			Where<MenuModel, Integer> where = queryBuilder.where();
			where.like(MenuModel.FIELD_USER, user);
			where.and();
			where.between(MenuModel.FIELD_DATE, begin, end);

			List<MenuModel> list = hdao.query(queryBuilder.prepare());
			
			List<Menu> res = new ArrayList<Menu>();
			for(MenuModel mm : list){
				Menu menu = null;
				try {
					menu = mapper.readValue(mm.getJson(), Menu.class);
//					if((menu.getDinner() != null 
//							&& menu.getDinner().getItems() != null
//							&& menu.getDinner().getItems().size() > 0) 
//							|| (menu.getLunch() != null
//									&& menu.getLunch().getItems() != null
//									&& menu.getLunch().getItems().size() > 0)){
					res.add(menu);
//					}
				} catch (JsonParseException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (JsonMappingException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
			}
			
			return res;

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	@Override
	public List<String> getHistoryMenuIn30Days(UserModel user) {
		try {
			Date today = new Date();
			String begin = DateUtils.defaultFormat(DateUtils.addDateDays(today, -1));
			String end = DateUtils.defaultFormat(DateUtils.addDateDays(today, -30));
			
			Dao<MenuModel, Integer> hdao = userHelper.getMenuDAO();

			QueryBuilder<MenuModel, Integer> queryBuilder = hdao
					.queryBuilder();
			Where<MenuModel, Integer> where = queryBuilder.where();
			where.like(MenuModel.FIELD_USER, user);
			where.and();
			where.between(MenuModel.FIELD_DATE, end, begin);

			List<MenuModel> list = hdao.query(queryBuilder.prepare());
			
			List<String> res = new ArrayList<String>();
			for(MenuModel mm : list){
				Menu menu = null;
				try {
					menu = mapper.readValue(mm.getJson(), Menu.class);
					res.add(menu.getMenudate());
				} catch (JsonParseException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (JsonMappingException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
			}
			
			return res;

		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}
	
	public List<String> getMenuHealthByDate(String courseInfoDesc){
		List<String> res = new ArrayList<String>();
		
		try {
			
			String[] dates = courseInfoDesc.split(":");
			if(dates != null){
				Dao<MenuModel, Integer> hdao = userHelper.getMenuDAO();
				
				for(String date : dates){
					int start = date.length() - 6;
					if(start >= 0){
						date = date.substring(start);
						if(date.indexOf("月") < 0) continue;
						date = date.replace("月", "-");
						date = date.replace("日", "");
						
						QueryBuilder<MenuModel, Integer> queryBuilder = hdao
								.queryBuilder();
						queryBuilder.orderBy(MenuModel.FIELD_DATE, false);
						
						Where<MenuModel, Integer> where = queryBuilder.where();
						where.like(MenuModel.FIELD_DATE, "%" + date);

						List<MenuModel> list = hdao.query(queryBuilder.prepare());
						
						if(list != null && list.size() > 0){
						Menu menu = null;
						try {
							menu = mapper.readValue(list.get(0).getJson(), Menu.class);
						} catch (JsonParseException e) {
							Log.e(LOG_TAG, e.getMessage());
						} catch (JsonMappingException e) {
							Log.e(LOG_TAG, e.getMessage());
						} catch (IOException e) {
							Log.e(LOG_TAG, e.getMessage());
						}
						if(menu != null){
							res.addAll(menu.getSmartParams().getHealthcondition());
						}
						}
					}
				}
			}
			
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return res;
	}

	@Override
	public boolean clearMenu(String date, UserModel user) {
		try {
			Dao<MenuModel, Integer> udao = userHelper.getMenuDAO();

			QueryBuilder<MenuModel, Integer> queryBuilder = udao.queryBuilder();
			Where<MenuModel, Integer> where = queryBuilder.where();
			where.gt(MenuModel.FIELD_DATE, date);
			where.and();
			where.eq(MenuModel.FIELD_USER, user);

			udao.delete(udao.query(queryBuilder.prepare()));

			return true;
		} catch (SQLException e) {

			Log.e(LOG_TAG, e.getMessage());
		}
		return false;
	}

}
