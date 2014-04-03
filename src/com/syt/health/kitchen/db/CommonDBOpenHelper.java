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
import com.syt.health.kitchen.db.common.CourseConditionModel;
import com.syt.health.kitchen.db.common.CacheFoodModel;
import com.syt.health.kitchen.db.common.HealthCondClassifyModel;
import com.syt.health.kitchen.db.common.HealthConditionModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.db.common.SysModel;
import com.syt.health.kitchen.db.common.TasteModel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CommonDBOpenHelper extends OrmLiteSqliteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "common.db";

	private static final AtomicInteger usageCounter = new AtomicInteger(0);
	// we do this so there is only one helper
	private static CommonDBOpenHelper helper = null;
	
	private Dao<HealthCondClassifyModel, Integer> healthCondClassifyDao;
	private Dao<CourseConditionModel, Integer> courseConditionDao;
	private Dao<TasteModel, Integer> tasteDao;
	private Dao<SysModel, Integer> sysDao;
	private Dao<HealthConditionModel, Integer> healthConditionDao;
	private Dao<NutrientModel, Integer> nutrientDao;

	public CommonDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized CommonDBOpenHelper getHelper(Context context) {
		if (helper == null) {
			helper = new CommonDBOpenHelper(context);
		}
		usageCounter.incrementAndGet();
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
	}

	public Dao<HealthCondClassifyModel, Integer> getHealthCondClassifyDao() throws SQLException {
		if(healthCondClassifyDao == null){
			healthCondClassifyDao = getDao(HealthCondClassifyModel.class);
    	}
		return healthCondClassifyDao;
	}

	public Dao<CourseConditionModel, Integer> getCourseConditionDao() throws SQLException {
		if(courseConditionDao == null){
			courseConditionDao = getDao(CourseConditionModel.class);
    	}
		return courseConditionDao;
	}

	public Dao<TasteModel, Integer> getTasteDao() throws SQLException {
		if(tasteDao == null){
			tasteDao = getDao(TasteModel.class);
    	}
		return tasteDao;
	}

	public Dao<SysModel, Integer> getSysDao() throws SQLException {
		if(sysDao == null){
			sysDao = getDao(SysModel.class);
    	}
		return sysDao;
	}

	public Dao<HealthConditionModel, Integer> getHealthConditionDao() throws SQLException {
		if(healthConditionDao == null){
			healthConditionDao = getDao(HealthConditionModel.class);
    	}
		return healthConditionDao;
	}
	
	

	public Dao<NutrientModel, Integer> getNutrientDao()  throws SQLException {
		if(nutrientDao == null){
			nutrientDao = getDao(NutrientModel.class);
    	}
		return nutrientDao;
	}

	@Override
	public void close() {
		super.close();
		
		this.courseConditionDao = null;
		this.healthCondClassifyDao = null;
		this.healthConditionDao = null;
		this.sysDao = null;
		this.tasteDao = null;
	}
}
