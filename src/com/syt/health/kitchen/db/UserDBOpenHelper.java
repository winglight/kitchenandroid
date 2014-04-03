package com.syt.health.kitchen.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.syt.health.kitchen.db.common.CacheCourseModel;
import com.syt.health.kitchen.db.common.CacheFoodModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBOpenHelper extends OrmLiteSqliteOpenHelper{
	
	public  static final int DATABASE_VERSION = 10;
	public static final String DATABASE_NAME = "user.db";
	
	private static final AtomicInteger usageCounter = new AtomicInteger(0);
	// we do this so there is only one helper
	private static UserDBOpenHelper helper = null;
	
	private Dao<UserModel, Integer> userDao;
	private Dao<MenuModel, Integer> menuDao;
	private Dao<CacheCourseModel, String> courseDao;
	private Dao<CacheFoodModel, String> foodDao;
	
	
    public UserDBOpenHelper(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public static synchronized UserDBOpenHelper getHelper(Context context) {
		if (helper == null) {
			helper = new UserDBOpenHelper(context);
		}
		usageCounter.incrementAndGet();
		return helper;
	}
    
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(UserDBOpenHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, UserModel.class);
			TableUtils.createTable(connectionSource, MenuModel.class);
			TableUtils.createTable(connectionSource, CacheCourseModel.class);
			TableUtils.createTable(connectionSource, CacheFoodModel.class);

			// here we try inserting data in the on-create as a test
//			Dao<UserModel, Integer> dao = getUserDAO();
//			// create some entries in the onCreate
//			 UserModel simple = new UserModel();
//			 simple.setName("Test User");
//			 simple.setEmail("test@fsl.com");
//			 simple.setPassword("1243124");
//			 simple.setPhoneNumber("2343834943");
//			 
//			dao.create(simple);
			
//			Log.i(UserDBOpenHelper.class.getName(), "created new entries in onCreate: " + simple.getName());
		} catch (SQLException e) {
			Log.e(UserDBOpenHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(UserDBOpenHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, UserModel.class, true);
			TableUtils.dropTable(connectionSource, MenuModel.class, true);
			
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(UserDBOpenHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

    public Dao<UserModel, Integer> getUserDAO() throws SQLException{
    	if(userDao == null){
    		userDao = getDao(UserModel.class);
    	}
    	return userDao;
    }
    
    public Dao<MenuModel, Integer> getMenuDAO() throws SQLException{
    	if(menuDao == null){
    		menuDao = getDao(MenuModel.class);
    	}
    	return menuDao;
    }
    
    public Dao<CacheCourseModel, String> getCourseDao() throws SQLException {
		if(courseDao == null){
			courseDao = getDao(CacheCourseModel.class);
    	}
		return courseDao;
	}

	public Dao<CacheFoodModel, String> getFoodDao() throws SQLException {
		if(foodDao == null){
			foodDao = getDao(CacheFoodModel.class);
    	}
		return foodDao;
	}
    
    @Override
	public void close() {
		super.close();
		userDao = null;
		menuDao = null;
		this.courseDao = null;
		this.foodDao = null;
	}
}
