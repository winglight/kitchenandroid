package com.syt.health.kitchen.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syt.health.kitchen.db.CommonDBOpenHelper;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.CacheCourseModel;
import com.syt.health.kitchen.db.common.CacheFoodModel;
import com.syt.health.kitchen.db.common.CourseConditionModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.db.common.SysModel;
import com.syt.health.kitchen.db.common.TasteModel;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseCondition;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.FoodCondition;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.MealCourse;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.utils.Utils;

public class ServiceImpl {

	private static final String LOG_TAG = "ServiceImpl";

	private IDBService dbService;
	private IRemoteService remoteService;
	private UserModel currentUser;
	private Context context;
	
	private Menu currentMenu;
	
	private final List<NutrientModel> nutrients;
	private final List<HealthCondClassifyModel> healthConditions;
	private List<Menu> menu7days;
	private List<String> menu30days;
	
	private ObjectMapper mapper;

	private static ServiceImpl instance;

	public static ServiceImpl getInstance(Context context) {
		if (instance == null) {
			initDBFile(context);
			
			instance = new ServiceImpl(context);
		}
		if (instance.remoteService == null) {
			instance.remoteService = RemoteServiceImpl.getInstance();
		}
		return instance;
	}
	
	private static void initDBFile(Context context) {
		File dbfile = context.getDatabasePath(CommonDBOpenHelper.DATABASE_NAME);
		// judge if there exists the db file
		if (!dbfile.exists()) {
			// no db file then copy init db file from assets
			InputStream in = null;
			try {
				in = context.getAssets().open(CommonDBOpenHelper.DATABASE_NAME);
				if (in != null) {
					dbfile.getParentFile().mkdirs();
					FileOutputStream writer =  new FileOutputStream(dbfile);
					IOUtils.write(IOUtils.toByteArray(in), writer);
				}
			} catch (Exception e) {
				Log.e(LOG_TAG , "copy init db exception:" + e.getMessage());
			}
		}
	}

	private ServiceImpl(Context context) {
		this.context = context;
		this.dbService = DBServiceImpl.getInstance(context);
		this.remoteService = RemoteServiceImpl.getInstance();
		
		this.nutrients = dbService.getAllNutrients();
		this.healthConditions = getDbService().getAllHealthCondition();
		
		this.mapper = new ObjectMapper();
		
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String sid = manager.getDeviceId();
		
		this.remoteService.setSid(sid);
	}

	public IDBService getDbService() {
		if (dbService == null) {
			dbService = DBServiceImpl.getInstance(context);
		}
		dbService.setCurrentUser(currentUser);
		return dbService;
	}

	private void setCurrentUser(UserModel currentUser) {
		this.currentUser = currentUser;
		this.dbService.setCurrentUser(currentUser);
	}

	/********** Remote Services ****************/
	public MessageModel<UserModel> loginByEmail(String email, String password) {
		MessageModel<UserModel> msg = new MessageModel<UserModel>();
		try {
			UserModel user = remoteService.login("", email, password);

			// save user into db
			if (!getDbService().createUser(user)) {
				msg.setFlag(false);
				msg.setErrorCode(ServiceException.ERROR_CODE_DB_EXCEPTION);
				return msg;
			}
			setCurrentUser(user);
			msg.setData(user);
			msg.setFlag(true);
		} catch (ServiceException e) {
			Log.e(LOG_TAG, "loginDirect error:" + e.getMessage());
			msg.setErrorCode(e.getErrorCode());
			msg.setMessage(e.getMessage());
		}
		return msg;
	}

	public MessageModel<UserModel> loginByPhone(String phone, String password) {

		MessageModel<UserModel> msg = new MessageModel<UserModel>();
		try {
			UserModel user = remoteService.login(phone, "", password);

			// save user into db
			if (!getDbService().createUser(user)) {
				msg.setFlag(false);
				msg.setErrorCode(ServiceException.ERROR_CODE_DB_EXCEPTION);
				return msg;
			}

			setCurrentUser(user);
			msg.setData(user);
			msg.setFlag(true);
		} catch (ServiceException e) {
			Log.e(LOG_TAG, "loginDirect error:" + e.getMessage());
			msg.setErrorCode(e.getErrorCode());
			msg.setMessage(e.getMessage());
		}
		return msg;
	}

	public MessageModel<String> forgetPasswordByEmail(String email) {
		MessageModel<String> msg = new MessageModel<String>();
		try {
			return remoteService.forgetPassword("", email);
		} catch (ServiceException e) {
			Log.e(LOG_TAG, "loginDirect error:" + e.getMessage());
			msg.setErrorCode(e.getErrorCode());
			msg.setMessage(e.getMessage());
		}
		return msg;
	}

	public MessageModel<String> forgetPasswordByPhone(String phone) {
		MessageModel<String> msg = new MessageModel<String>();
		try {
			return remoteService.forgetPassword(phone, "");
		} catch (ServiceException e) {
			Log.e(LOG_TAG, "loginDirect error:" + e.getMessage());
			msg.setErrorCode(e.getErrorCode());
			msg.setMessage(e.getMessage());
		}
		return msg;
	}

	public MessageModel<String> register(UserModel user) {
		MessageModel<String> msg = new MessageModel<String>();
		/**
		 * // 1.check duplicated user if (user.getEmail() != null) { if
		 * (dbService.queryUserByEmail(user.getEmail()) != null) {
		 * message.setMessage("Duplicated user's e-mail:" + user.getEmail());
		 * return message; } }
		 * 
		 * if (user.getPhoneNumber() != null) { if
		 * (dbService.queryUserByEmail(user.getPhoneNumber()) != null) {
		 * message.setMessage("Duplicated user's phone number:" +
		 * user.getPhoneNumber()); return message; } }
		 * 
		 * // 2.insert a user record if (dbService.createUser(user)) {
		 * 
		 * message.setFlag(true); } else {
		 * message.setMessage("insert sql error"); }
		 **/
		try {
			msg = remoteService.register(user);

			if (msg.isFlag()) {
				// save user into db

				if (!getDbService().createUser(user)) {
					msg.setFlag(false);
					msg.setErrorCode(ServiceException.ERROR_CODE_DB_EXCEPTION);
					return msg;
				}

				msg.setFlag(true);
			}
		} catch (ServiceException e) {
			Log.e(LOG_TAG, "loginDirect error:" + e.getMessage());
			msg.setErrorCode(e.getErrorCode());
			msg.setMessage(e.getMessage());
		}

		return msg;
	}

	public void close() {
		if (dbService != null) {
			dbService.close();
			dbService = null;
		}
	}

	public MessageModel<UserModel> loginDirect(String sid) {
		MessageModel<UserModel> msg = new MessageModel<UserModel>();
		try {
			UserModel user = remoteService.loginDirect(sid);

			// query user from local db
			UserModel user2 = getDbService().queryUserBySid(sid);
			if (user2 == null) {
				// save user into db
				if (!getDbService().createUser(user)) {
					msg.setFlag(false);
					msg.setErrorCode(ServiceException.ERROR_CODE_DB_EXCEPTION);
					return msg;
				} else {
					user = getDbService().queryUserBySid(sid);
				}
			} else {
				user = user2;
			}

			setCurrentUser(user);
			msg.setData(user);
			msg.setFlag(true);
		} catch (ServiceException e) {
			Log.e(LOG_TAG, "loginDirect error:" + e.getMessage());
			msg.setErrorCode(e.getErrorCode());
			msg.setMessage(e.getMessage());
		}
		return msg;
	}

	/**
	 * To get four meals of one day, including breakfast, lunch, dinner, fruits
	 * 1.get meals from DB, if exist, return results 2.if conditions exist,
	 * generate meals and save menu by RPC 3.save meals into local DB 4.if
	 * conditions not exist, return null, error code: ERROR_CODE_NO_CONDITION
	 * 
	 * @param tcb
	 *            asynchronous callback
	 */
	public void fetchMealsByDate(TaskCallBack<String, MessageModel<Menu>> tcb) {
		MyTask<String, MessageModel<Menu>> task = new MyTask<String, MessageModel<Menu>>(
				tcb) {

			@Override
			protected MessageModel<Menu> doInBackground(String... params) {
				MessageModel<Menu> msg = new MessageModel<Menu>();
				try {
					Menu menu = null;
					String date = params[0];
					// 1.query meals from local db
					menu = getDbService().queryMealsByDate(date);

					if (menu != null) {
						// 1.1
						if(menu.getNutrients() == null){
							List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(date, nutrients);
							menu.setNutrients(nList);
						}
						currentMenu = menu;
						msg.setData(menu);
					} else {
						// 1.2
						if (currentUser != null) {
							if(currentUser.getSmartParams() == null){
								GenerateCondition gc = new GenerateCondition();
								gc.setPeople(1);
								
								getDbService().saveSmartParams(gc, currentUser);
								currentUser.setObjSmartParams(gc);
							}
							// 2.1 get menu
//							menu = remoteService.fetchMealsByDate(date);

							// 2.2
//							if (menu == null) {
								menu = remoteService.generateMenuByDate(date,
										currentUser.getObjSmartParams());
//							}
								
							//2.3 fetch menu nutrient
								List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(date, nutrients);
								menu.setNutrients(nList);

							currentMenu = menu;
							msg.setData(menu);
							
							menu7days = null;

							// 3.
							boolean flag = getDbService().saveMenu(menu,
									currentUser, true);
							if (!flag) {
								msg.setFlag(false);
								msg.setErrorCode(ServiceException.ERROR_CODE_DB_EXCEPTION);
								return msg;
							}
						} else {
							// 4.
							msg.setFlag(false);
							msg.setErrorCode(ServiceException.BUSINESS_CODE_NO_CONDITION);
							return msg;
						}
					}
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "fetchMealsByDate error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	/**
	 * Fetch courses by conditions
	 * 
	 * @param tcb
	 *            asynchronous callback
	 */
	public void fetchCourseByParam(
			TaskCallBack<CourseCondition, MessageModel<List<Course>>> tcb) {
		MyTask<CourseCondition, MessageModel<List<Course>>> task = new MyTask<CourseCondition, MessageModel<List<Course>>>(
				tcb) {

			@Override
			protected MessageModel<List<Course>> doInBackground(
					CourseCondition... params) {
				MessageModel<List<Course>> msg = new MessageModel<List<Course>>();
				try {
					CourseCondition condition = params[0];
					if(condition.getMode() == 0){
					msg.setData(remoteService.fetchCourseByParams(condition));
					}else if(condition.getMode() == 1){
						msg.setData(remoteService.fetchCourseByCals(condition.getCals(), condition.isAscendOrder(), condition.getPage()));
					}else{
						msg.setData(remoteService.fetchCourseByFood(condition.getFoodName(), condition.getCoursecond(), condition.getPage()));
					}
					// msg.setData(generateFakeData());
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "fetchMealsByDate error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}
	
	public void fetchFoodByParam(
			TaskCallBack<FoodCondition, MessageModel<List<Food>>> tcb) {
		MyTask<FoodCondition, MessageModel<List<Food>>> task = new MyTask<FoodCondition, MessageModel<List<Food>>>(
				tcb) {

			@Override
			protected MessageModel<List<Food>> doInBackground(
					FoodCondition... params) {
				MessageModel<List<Food>> msg = new MessageModel<List<Food>>();
				try {
					FoodCondition condition = params[0];
					switch(condition.getMode()){
					case 0:{
						msg.setData(remoteService.fetchFoodByHealth(condition.getHealthcondition(), condition.getFilter(), condition.getPage()));
						break;
					}
					case 1:{
						msg.setData(remoteService.fetchFoodByNutrient(condition.getNutrients(), condition.getFilter(), condition.getPage()));
						break;
					}
					case 2:{
						msg.setData(remoteService.fetchFoodByCals(condition.getCals(), condition.isAscendOrder(), condition.getPage()));
						break;
					}
						
					}
					
					// msg.setData(generateFakeData());
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "fetchMealsByDate error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	/**
	 * Query course by condition: like %key%
	 * 
	 * @param key
	 *            query condition key words
	 * @return list of courses
	 */
	public void queryCourseByLikeWords(final int page, final boolean filtercond,
			TaskCallBack<String, MessageModel<List<Course>>> tcb) {
		MyTask<String, MessageModel<List<Course>>> task = new MyTask<String, MessageModel<List<Course>>>(
				tcb) {

			@Override
			protected MessageModel<List<Course>> doInBackground(
					String... params) {
				MessageModel<List<Course>> msg = new MessageModel<List<Course>>();
				try {
					String condition = params[0];
					msg.setData(remoteService.queryCourseByKeyword(condition,filtercond,
							page));
					// msg.setData(generateFakeData());
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "fetchMealsByDate error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	public void queryFoodByLikeWords(final int page,
			TaskCallBack<String, MessageModel<List<Food>>> tcb) {
		MyTask<String, MessageModel<List<Food>>> task = new MyTask<String, MessageModel<List<Food>>>(
				tcb) {

			@Override
			protected MessageModel<List<Food>> doInBackground(String... params) {
				MessageModel<List<Food>> msg = new MessageModel<List<Food>>();
				try {
					String condition = params[0];
					msg.setData(remoteService.queryFoodByKeyword(condition,
							page));
					// msg.setData(generateFakeData());
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "fetchMealsByDate error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}
	

	// private List<Course> generateFakeData() {
	// ArrayList<Course> list = new ArrayList<Course>();
	// for (int i = 0; i < 10; i++) {
	// Course c = new Course();
	// c.setId(i + "");
	// c.setName("Course " + i);
	// list.add(c);
	// }
	//
	// return list;
	// }

	/**
	 * Get a course by id
	 * 
	 * @param tcb
	 *            asynchronous callback
	 */
	public void getCourseById(TaskCallBack<String, MessageModel<Course>> tcb) {
		MyTask<String, MessageModel<Course>> task = new MyTask<String, MessageModel<Course>>(
				tcb) {

			@Override
			protected MessageModel<Course> doInBackground(String... params) {
				MessageModel<Course> msg = new MessageModel<Course>();
				try {
					String id = params[0];
					// 1.query course in local db
					CacheCourseModel ccm = getDbService().getCourseById(id);
					if (ccm != null) {
						String json = ccm.getJson();
						Course course = null;
						try {
							course = mapper.readValue(json, Course.class);
						} catch (JsonParseException e) {
							Log.e(LOG_TAG, e.getMessage());
						} catch (JsonMappingException e) {
							Log.e(LOG_TAG, e.getMessage());
						} catch (IOException e) {
							Log.e(LOG_TAG, e.getMessage());
						}
						msg.setData(course);
					} else {
						Course course = remoteService.queryCourseById(id);
						ccm = new CacheCourseModel();
						ccm.setId(course.getId());
						ccm.setJson(course);
						getDbService().saveCacheCourse(ccm);
						msg.setData(course);
					}
					msg.setFlag(true);
				} catch (ServiceException e) {  
					Log.e(LOG_TAG, "getCourseById error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	public void getCoursesByIds(
			TaskCallBack<List<Course>, MessageModel<List<Course>>> tcb) {
		MyTask<List<Course>, MessageModel<List<Course>>> task = new MyTask<List<Course>, MessageModel<List<Course>>>(
				tcb) {

			@Override
			protected MessageModel<List<Course>> doInBackground(
					List<Course>... params) {
				MessageModel<List<Course>> msg = new MessageModel<List<Course>>();
				try {
					List<Course> list = params[0];
					List<Course> list2 = new ArrayList<Course>();
					for (Course course : list) {
						Course course2 = null;
						
						// 1.query course in local db
						CacheCourseModel ccm = getDbService().getCourseById(course
								.getId());
						if (ccm != null) {
							String json = ccm.getJson();
							 try {
									course2 = mapper.readValue(json, Course.class);
								} catch (JsonParseException e) {
									Log.e(LOG_TAG, e.getMessage());
								} catch (JsonMappingException e) {
									Log.e(LOG_TAG, e.getMessage());
								} catch (IOException e) {
									Log.e(LOG_TAG, e.getMessage());
								}

						} else {
							course2 = remoteService.queryCourseById(course
									.getId());
							if(course2 != null){
							ccm = new CacheCourseModel();
							ccm.setId(course2.getId());
							ccm.setJson(course2);
									getDbService().saveCacheCourse(ccm);
							}else{
								throw new ServiceException("getCoursesByIds error: course2 is null!");
							}
						}
						
						list2.add(course2);
					}
					msg.setData(list2);
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "getCoursesByIds error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	public void getFoodById(TaskCallBack<String, MessageModel<Food>> tcb) {
		MyTask<String, MessageModel<Food>> task = new MyTask<String, MessageModel<Food>>(
				tcb) {

			@Override
			protected MessageModel<Food> doInBackground(String... params) {
				MessageModel<Food> msg = new MessageModel<Food>();
				try {
					String id = params[0];
					// 1.query food in local db
					CacheFoodModel ccm = getDbService().getFoodById(id);
					if (ccm != null) {
						String json = ccm.getJson();
						Food food = null;
						try {
							food = mapper.readValue(json, Food.class);
						} catch (JsonParseException e) {
							Log.e(LOG_TAG, e.getMessage());
						} catch (JsonMappingException e) {
							Log.e(LOG_TAG, e.getMessage());
						} catch (IOException e) {
							Log.e(LOG_TAG, e.getMessage());
						}
						msg.setData(food);
					} else {
						Food food = remoteService.queryFoodById(id);
						CacheFoodModel cfm = new CacheFoodModel();
						cfm.setId(food.getId());
						cfm.setJson(food);
						msg.setData(food);
						getDbService().saveCacheFood(cfm);
					}

					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "getFoodById error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}
	
	public void cacheFoods(TaskCallBack<List<String>, MessageModel<String>> tcb) {
		MyTask<List<String>, MessageModel<String>> task = new MyTask<List<String>, MessageModel<String>>(
				tcb) {

			@Override
			protected MessageModel<String> doInBackground(List<String>... params) {
				MessageModel<String> msg = new MessageModel<String>();
				try {
					List<String> list = params[0];
					for(String id : list){
					// 1.query food in local db
					CacheFoodModel ccm = getDbService().getFoodById(id);
					if (ccm != null) {
					} else {
						Food food = remoteService.queryFoodById(id);
						CacheFoodModel cfm = new CacheFoodModel();
						cfm.setId(food.getId());
						cfm.setJson(food);
						getDbService().saveCacheFood(cfm);
					}
					}

					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "cacheFoods error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	/**
	 * Update or create a Meal with list of courses. If id is null, it should be
	 * created new one, otherwise, updated. We should save menu into local db
	 * after finishing remote invoke.
	 * 
	 * @param date
	 *            menu date
	 * @param tcb
	 * @throws ServiceException
	 */
	// public void updateOrNewMeal(final String date,
	// TaskCallBack<Meal, MessageModel<Meal>> tcb) {
	// MyTask<Meal, MessageModel<Meal>> task = new MyTask<Meal,
	// MessageModel<Meal>>(
	// tcb) {
	//
	// @Override
	// protected MessageModel<Meal> doInBackground(Meal... params) {
	// MessageModel<Meal> msg = new MessageModel<Meal>();
	// try {
	// // 1.remote invoke to update or create a meal
	// Meal meal = params[0];
	// msg.setData(remoteService.updateOrNewMeal(meal));
	//
	// // 2.query menu from local db
	// Menu menu = getDbService().queryMealsByDate(date);
	//
	// // 3.save menu to local db
	//
	// menu.setMeal(meal);
	// if(!getDbService().saveMenu(menu, currentUser)){
	// Log.e(LOG_TAG, "updateOrNewMeal db error!");
	// msg.setFlag(false);
	// msg.setMessage("updateOrNewMeal db error!");
	// return msg;
	// }
	//
	// msg.setFlag(true);
	// } catch (ServiceException e) {
	// Log.e(LOG_TAG, "fetchMealsByDate error:" + e.getMessage());
	// msg.setFlag(false);
	// msg.setErrorCode(e.getErrorCode());
	// }
	// return msg;
	// }
	// };
	// task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	// }

	/**
	 * Add a course to the meal and save local menu
	 * 
	 * @param date
	 * @param courseId
	 * @param tcb
	 */
//	public void addCourseToMeal(final String date, final String courseId,
//			TaskCallBack<Meal, MessageModel<Course>> tcb) {
//		MyTask<Meal, MessageModel<Course>> task = new MyTask<Meal, MessageModel<Course>>(
//				tcb) {
//
//			@Override
//			protected MessageModel<Course> doInBackground(Meal... params) {
//				MessageModel<Course> msg = new MessageModel<Course>();
//				try {
//					// 1.remote invoke to update or create a meal
//					Meal meal = params[0];
//					remoteService.addCourseToMeal(meal.getId(),
//							courseId, -1);
//					MealCourse mc = new MealCourse();
//					mc.setCourse(getDbService().getCourseById(courseId).);
//					mc.setQuantity(1);
//					mc.setUnit(ret.getUnit());
//					List<MealCourse> items = meal.getItems();
//					if (items == null) {
//						items = new ArrayList<MealCourse>();
//					}
//					items.add(mc);
//					meal.setComments(null);
//
//					msg.setData(ret);
//
//					// 2.query menu from local db
//					Menu menu = getDbService().queryMealsByDate(date);
//
//					// 3.save menu to local db
//					meal.setIsselect(1);
//					menu.setMeal(meal);
//					// 4. set menu comments as null
//					menu.setComments("");
//					List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(date, nutrients);
//					menu.setNutrients(nList);
//					
//					currentMenu = menu;
//
//					if (!getDbService().saveMenu(menu, currentUser, false)) {
//						Log.e(LOG_TAG, "addCourseToMeal db error!");
//						msg.setFlag(false);
//						msg.setMessage("addCourseToMeal db error!");
//						return msg;
//					}
//
//					msg.setFlag(true);
//				} catch (ServiceException e) {
//					Log.e(LOG_TAG, "addCourseToMeal error:" + e.getMessage());
//					msg.setFlag(false);
//					msg.setErrorCode(e.getErrorCode());
//					msg.setMessage(e.getMessage());
//				}
//				return msg;
//			}
//		};
//		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
//	}
	
	
	public void addCourseToMeal(final String date, final MealCourse mc,
			TaskCallBack<Meal, MessageModel<Course>> tcb) {
		MyTask<Meal, MessageModel<Course>> task = new MyTask<Meal, MessageModel<Course>>(
				tcb) {

			@Override
			protected MessageModel<Course> doInBackground(Meal... params) {
				MessageModel<Course> msg = new MessageModel<Course>();
				try {
					// 1.remote invoke to update or create a meal
					Meal meal = params[0];
					remoteService.addCourseToMeal(meal.getId(),
							mc.getCourse().getId(), mc.getQuantity());
					
					List<MealCourse> items = meal.getItems();
					if (items == null) {
						items = new ArrayList<MealCourse>();
					}
					Utils.listRemove(mc, items);
					if(mc.getQuantity() < 0){
						mc.setQuantity(1);
					}
					items.add(mc);
					meal.setComments(null);

					msg.setData(mc.getCourse());

					// 2.query menu from local db
					Menu menu = getDbService().queryMealsByDate(date);

					// 3.save menu to local db
					meal.setIsselect(1);
					menu.setMeal(meal);
					// 4. set menu comments as null
					menu.setComments("");
					
					List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(date, nutrients);
					menu.setNutrients(nList);
					
					currentMenu = menu;
					
					menu7days = null;

					if (!getDbService().saveMenu(menu, currentUser, false)) {
						Log.e(LOG_TAG, "addCourseToMeal db error!");
						msg.setFlag(false);
						msg.setMessage("addCourseToMeal db error!");
						return msg;
					}

					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "addCourseToMeal error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}
	
	public void modifyCourseToMeal(final String date, final MealCourse mc,
			TaskCallBack<Meal, MessageModel<Meal>> tcb) {
		MyTask<Meal, MessageModel<Meal>> task = new MyTask<Meal, MessageModel<Meal>>(
				tcb) {

			@Override
			protected MessageModel<Meal> doInBackground(Meal... params) {
				MessageModel<Meal> msg = new MessageModel<Meal>();
				try {
					// 1.remote invoke to update or create a meal
					Meal meal = params[0];
					remoteService.modifyCourseToMeal(meal.getId(),
							mc.getCourse().getId(), mc.getQuantity());
					List<MealCourse> items = meal.getItems();
					if (items == null) {
						items = new ArrayList<MealCourse>();
					}
					Utils.listRemove(mc, items);
					items.add(mc);
					meal.setComments(null);

					// 2.query menu from local db
					Menu menu = getDbService().queryMealsByDate(date);

					// 3.save menu to local db
					meal.setIsselect(1);
					menu.setMeal(meal);
					// 4. set menu comments as null
					menu.setComments(null);
					
					List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(date, nutrients);
					menu.setNutrients(nList);
					
					currentMenu = menu;
					
					menu7days = null;

					if (!getDbService().saveMenu(menu, currentUser, false)) {
						Log.e(LOG_TAG, "modifyCourseToMeal db error!");
						msg.setFlag(false);
						msg.setMessage("modifyCourseToMeal db error!");
						return msg;
					}

					msg.setData(meal);
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "modifyCourseToMeal error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	/**
	 * Remove a course from the meal and save local menu
	 * 
	 * @param date
	 * @param courseId
	 * @param tcb
	 */
	public void removeCourseFromMeal(final String date, final MealCourse mc,
			TaskCallBack<Meal, MessageModel<Meal>> tcb) {
		MyTask<Meal, MessageModel<Meal>> task = new MyTask<Meal, MessageModel<Meal>>(
				tcb) {

			@Override
			protected MessageModel<Meal> doInBackground(Meal... params) {
				MessageModel<Meal> msg = new MessageModel<Meal>();
				try {
					// 1.remote invoke to update or create a meal
					Meal meal = params[0];
					remoteService.removeCourseFromMeal(meal.getId(), mc
							.getCourse().getId());
					Utils.listRemove(mc, meal.getItems());
					meal.setComments(null);

					// 2.query menu from local db
					Menu menu = getDbService().queryMealsByDate(date);

					// 3.save menu to local db
					meal.setIsselect(1);
					menu.setMeal(meal);

					// 4. set menu comments as null
					menu.setComments(null);
					
					List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(date, nutrients);
					menu.setNutrients(nList);
					
					currentMenu = menu;
					
					menu7days = null;
					
					if (!getDbService().saveMenu(menu, currentUser, false)) {
						Log.e(LOG_TAG, "removeCourseFromMeal db error!");
						msg.setFlag(false);
						msg.setMessage("removeCourseFromMeal db error!");
						return msg;
					} else {
						msg.setData(meal);
					}

					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG,
							"removeCourseFromMeal error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	public void generateMealByHealthCondition(final Menu menu,
			TaskCallBack<Meal, MessageModel<Meal>> tcb) {
		MyTask<Meal, MessageModel<Meal>> task = new MyTask<Meal, MessageModel<Meal>>(
				tcb) {

			@Override
			protected MessageModel<Meal> doInBackground(Meal... params) {
				MessageModel<Meal> msg = new MessageModel<Meal>();
				try {
					if (currentUser != null
							&& currentUser.getSmartParams() != null) {
						// 2.
						Meal meal = params[0];
						Meal meal2 = remoteService
								.generateMealByHealthCondition(meal,
										menu.getSmartParams());

						msg.setData(meal2);

						// 3.
						menu.setMeal(meal2);
						
						// 4. set menu comments as null
						menu.setComments(null);
						
						// 5.
						List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(menu.getMenudate(), nutrients);
						menu.setNutrients(nList);
						
						currentMenu = menu;
						
						menu7days = null;
						
						boolean flag = getDbService().saveMenu(menu,
								currentUser, false);
						if (!flag) {
							msg.setFlag(false);
							msg.setErrorCode(ServiceException.ERROR_CODE_DB_EXCEPTION);
							return msg;
						}
					} else {
						// 4.
						msg.setFlag(false);
						msg.setErrorCode(ServiceException.BUSINESS_CODE_NO_CONDITION);
						return msg;
					}
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "generateMealByHealthCondition error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}
	
	public void generateMenuByHealthCondition(final String date,
			TaskCallBack<String, MessageModel<Menu>> tcb) {
		MyTask<String, MessageModel<Menu>> task = new MyTask<String, MessageModel<Menu>>(
				tcb) {

			@Override
			protected MessageModel<Menu> doInBackground(String... params) {
				MessageModel<Menu> msg = new MessageModel<Menu>();
				try {
					if (currentUser != null
							&& currentUser.getSmartParams() != null) {
						String date = params[0];
						Menu menu = remoteService.generateMenuByDate(date,
								currentUser.getObjSmartParams());

						List<NutrientModel> nList = remoteService.getMenuNutrientsByDate(date, nutrients);
						menu.setNutrients(nList);
						
						msg.setData(menu);
						
						currentMenu = menu;
						
						//delete the future menus
						getDbService().clearMenu(date,
								currentUser);
						
						menu7days = null;

						boolean flag = getDbService().saveMenu(menu,
								currentUser, true);
						if (!flag) {
							msg.setFlag(false);
							msg.setErrorCode(ServiceException.ERROR_CODE_DB_EXCEPTION);
							return msg;
						}
					} else {
						// 4.
						msg.setFlag(false);
						msg.setErrorCode(ServiceException.BUSINESS_CODE_NO_CONDITION);
						return msg;
					}
					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "generateMenuByHealthCondition error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	/**
	 * Create a menu on a date with full null meals from the server
	 * 
	 * @param tcb
	 *            date is input parameter, menu is output parameter
	 */
	public void createMenuByDate(TaskCallBack<String, MessageModel<Menu>> tcb) {
		MyTask<String, MessageModel<Menu>> task = new MyTask<String, MessageModel<Menu>>(
				tcb) {

			@Override
			protected MessageModel<Menu> doInBackground(String... params) {
				MessageModel<Menu> msg = new MessageModel<Menu>();
				try {
					String date = params[0];
					// 1.query meals from local db
					Menu menu = remoteService.createMenuByDate(date);

					msg.setData(menu);

					currentMenu = menu;
					
					// 2.save menu into local db
					if (!getDbService().saveMenu(menu, currentUser, true)) {
						msg.setMessage("db save menu error");
						return msg;
					}

					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG, "createMenuByDate error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
					msg.setMessage(e.getMessage());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());
	}

	/**
	 * Get menu comments, if not available on local, then get it from the server
	 * 
	 * @param tcb
	 *
	public void getMenuCommentByDate(
			TaskCallBack<String, MessageModel<String>> tcb) {
		MyTask<String, MessageModel<String>> task = new MyTask<String, MessageModel<String>>(
				tcb) {

			@Override
			protected MessageModel<String> doInBackground(String... params) {
				MessageModel<String> msg = new MessageModel<String>();
				try {

					String date = params[0];

					// 1.get comments from local db
					Menu menu = getDbService().queryMealsByDate(date);
					String comments = menu.getComments();

					if (comments == null || comments.length() == 0) {
						// 2.query comments from server
						comments = remoteService.getMenuCommentsByDate(date);

						// 3.save comments into local db
						menu.setComments(comments);
						
						getDbService().saveMenu(menu, currentUser, false);

					}
					
					currentMenu = menu;
					
					msg.setData(comments);

					msg.setFlag(true);
				} catch (ServiceException e) {
					Log.e(LOG_TAG,
							"getMenuCommentByDate error:" + e.getMessage());
					msg.setFlag(false);
					msg.setErrorCode(e.getErrorCode());
				}
				return msg;
			}
		};
		task.executeOnExecutor(MyTask.DUAL_THREAD_EXECUTOR, tcb.getParameters());

	}*/

	/************ DB services *************/

	public List<HealthCondClassifyModel> getAllHealthCondition() {
		return healthConditions;
	}

	public List<String> getAllCourseCondition() {
		List<CourseConditionModel> list = getDbService()
				.getAllCourseCondition();
		ArrayList<String> ret = new ArrayList<String>();
		for (CourseConditionModel ccm : list) {
			ret.add(ccm.getName());
		}
		return ret;
	}

	public List<String> getAllTaste() {
		List<TasteModel> list = getDbService().getAllTaste();
		ArrayList<String> ret = new ArrayList<String>();
		for (TasteModel ccm : list) {
			ret.add(ccm.getName());
		}
		return ret;
	}

	public SysModel getDbSysInfo() {
		return getDbService().getDbSysInfo();
	}

	/**
	 * Save health conditions and persons, if health condition changed, return
	 * true, vice versa. Exception: if old health conditions is null, return
	 * false.
	 * 
	 * @param params
	 *            health condition, including persons
	 * @return
	 */
	public boolean saveSmartParams(GenerateCondition params) {
		boolean flag = false;
		if (currentUser.getObjSmartParams() != null && params != null) {
			int oldPerson = currentMenu.getSmartParams().getPeople();
			int newPerson = params.getPeople();
			if (oldPerson != newPerson)
				flag = true;

			if (!flag) {
				List<String> oldHealths = params.getHealthcondition();
				List<String> newHealths = currentMenu.getSmartParams()
						.getHealthcondition();
				if (oldHealths != null && newHealths != null && oldHealths.size() == newHealths.size()) {
					for (String condition : oldHealths) {
						if (!Utils.listContains(condition, newHealths)) {
							flag = true;
							break;
						}
					}
				}else{
					flag = true;
				}
			}
		}else{
			flag = true;
		}
		getDbService().saveSmartParams(params, currentUser);
		currentUser.setObjSmartParams(params);

		return flag;
	}

	public void saveFoodList(List<CourseFood> list, String desc) {
		getDbService().saveFoodList(list, desc, currentUser);
		currentUser.setObjFoodList(list);
		if(desc != null){
		currentUser.setFoodListDesc(desc);
		}
	}

	/**
	 * Get menus displayed in buy list
	 * 
	 * @return
	 */
	public List<Menu> getMenuIn7Days() {
		if(this.menu7days == null){
			this.menu7days = getDbService().getMenuIn7Days(currentUser);
		}
		return this.menu7days; 
	}

	/**
	 * Get menus displayed in coverflow
	 * 
	 * @return
	 */
	public List<String> getMenuIn30Days() {
		if(this.menu30days == null){
			this.menu30days = getDbService().getHistoryMenuIn30Days(currentUser);
		}
		return menu30days;
	}
	
	public List<String> getMenuHealthByDate(String courseInfoDesc) {
			return getDbService().getMenuHealthByDate(courseInfoDesc);
	}

	/**
	 * Get food list without login
	 * 
	 * @param sid
	 * @return
	 */
	public List<CourseFood> getFoodList(String sid) {
		List<CourseFood> res = null;
		// query user from local db
		UserModel user2 = getDbService().queryUserBySid(sid);
		
		if (user2 != null) {
			res = user2.getObjFoodList();
			setCurrentUser(user2);
		}

		return res;
	}
	
	public boolean hasFoodList(String sid) {
		List<CourseFood> res = null;
		// query user from local db
		UserModel user2 = getDbService().queryUserBySid(sid);
		
		if (user2 != null) {
			res = user2.getObjFoodList();
			if(res != null && res.size() > 0){
				return true;
			}
		}

		return false;
	}

	/************ End of DB services *************/

	public void setURL(String url) {
		remoteService.setBaseUrl(url);
	}

	public String getURL() {
		return remoteService.getBaseUrl();
	}

	public UserModel getCurrentUser() {
		return currentUser;
	}

	public Menu getCurrentMenu() {
		return currentMenu;
	}

	public void setCurrentMenu(Menu currentMenu) {
		this.currentMenu = currentMenu;
	}

	public List<NutrientModel> getNutrients() {
		return nutrients;
	}

}
